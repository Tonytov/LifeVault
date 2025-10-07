// 🔐 Сервис аутентификации LifeVault

import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { v4 as uuidv4 } from 'uuid';
import { prisma } from '@/utils/database';
import { config } from '@/utils/config';
import { appLogger } from '@/utils/logger';
import { smsService } from './sms.service';
import type { 
  LifeVaultJwtPayload, 
  SMSVerificationRequest, 
  SMSVerificationResponse 
} from '@/types';

export class AuthService {
  
  // 📱 Отправка SMS кода для входа/регистрации
  async sendVerificationCode(phoneNumber: string): Promise<{ success: boolean; message: string }> {
    try {
      // Нормализуем номер телефона
      const normalizedPhone = this.normalizePhoneNumber(phoneNumber);
      
      // Генерируем 6-значный код
      const verificationCode = this.generateVerificationCode();
      const codeExpiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 минут
      
      // Сохраняем или обновляем пользователя с кодом
      await prisma.user.upsert({
        where: { phoneNumber: normalizedPhone },
        update: {
          verificationCode,
          codeExpiresAt,
        },
        create: {
          phoneNumber: normalizedPhone,
          verificationCode,
          codeExpiresAt,
          isPhoneVerified: false,
        },
      });
      
      // Отправляем SMS
      const smsResult = await smsService.sendVerificationCode(normalizedPhone, verificationCode);
      
      if (smsResult.success) {
        appLogger.auth.register(normalizedPhone);
        return {
          success: true,
          message: 'Код верификации отправлен на ваш телефон'
        };
      } else {
        throw new Error('Не удалось отправить SMS');
      }
      
    } catch (error) {
      appLogger.error.external('SMS', error as Error);
      return {
        success: false,
        message: 'Ошибка при отправке кода верификации'
      };
    }
  }
  
  // ✅ Верификация SMS кода и выдача токенов
  async verifyCode(data: SMSVerificationRequest): Promise<SMSVerificationResponse> {
    try {
      const normalizedPhone = this.normalizePhoneNumber(data.phoneNumber);
      
      // Находим пользователя с кодом
      const user = await prisma.user.findUnique({
        where: { phoneNumber: normalizedPhone }
      });
      
      if (!user) {
        return {
          success: false,
          message: 'Пользователь не найден'
        };
      }
      
      // Проверяем код и срок действия
      if (!user.verificationCode || user.verificationCode !== data.code) {
        appLogger.auth.verify(normalizedPhone, false);
        return {
          success: false,
          message: 'Неверный код верификации'
        };
      }
      
      if (!user.codeExpiresAt || user.codeExpiresAt < new Date()) {
        return {
          success: false,
          message: 'Код верификации истек'
        };
      }
      
      // Верификация успешна - очищаем код и помечаем телефон как подтвержденный
      const updatedUser = await prisma.user.update({
        where: { id: user.id },
        data: {
          isPhoneVerified: true,
          verificationCode: null,
          codeExpiresAt: null,
          lastLoginAt: new Date(),
        },
      });
      
      // Генерируем JWT токены
      const { accessToken, refreshToken } = await this.generateTokenPair(updatedUser);
      
      appLogger.auth.verify(normalizedPhone, true);
      appLogger.auth.login(normalizedPhone, true);
      
      return {
        success: true,
        message: 'Аутентификация успешна',
        token: accessToken,
        refreshToken: refreshToken
      };
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/verify');
      return {
        success: false,
        message: 'Ошибка при верификации кода'
      };
    }
  }
  
  // 🔄 Обновление access токена через refresh токен
  async refreshAccessToken(refreshTokenString: string): Promise<SMSVerificationResponse> {
    try {
      // Проверяем refresh токен в базе
      const storedToken = await prisma.refreshToken.findUnique({
        where: { token: refreshTokenString },
        include: { user: true }
      });
      
      if (!storedToken || storedToken.expiresAt < new Date()) {
        return {
          success: false,
          message: 'Недействительный или истекший refresh токен'
        };
      }
      
      // Верифицируем JWT
      try {
        jwt.verify(refreshTokenString, config.jwtSecret) as LifeVaultJwtPayload;
      } catch {
        return {
          success: false,
          message: 'Недействительный refresh токен'
        };
      }
      
      // Генерируем новую пару токенов
      const { accessToken, refreshToken } = await this.generateTokenPair(storedToken.user);
      
      // Удаляем старый refresh токен
      await prisma.refreshToken.delete({
        where: { id: storedToken.id }
      });
      
      return {
        success: true,
        message: 'Токен обновлен',
        token: accessToken,
        refreshToken: refreshToken
      };
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/refresh');
      return {
        success: false,
        message: 'Ошибка при обновлении токена'
      };
    }
  }
  
  // 🚪 Выход из системы
  async logout(refreshTokenString: string): Promise<{ success: boolean; message: string }> {
    try {
      // Удаляем refresh токен из базы
      await prisma.refreshToken.deleteMany({
        where: { token: refreshTokenString }
      });
      
      return {
        success: true,
        message: 'Выход выполнен успешно'
      };
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/logout');
      return {
        success: false,
        message: 'Ошибка при выходе из системы'
      };
    }
  }
  
  // 🔐 Генерация пары токенов
  private async generateTokenPair(user: any) {
    const payload: LifeVaultJwtPayload = {
      userId: user.id,
      phoneNumber: user.phoneNumber,
      type: 'access'
    };
    
    // Access токен (короткий срок жизни)
    const accessToken = jwt.sign(
      payload,
      config.jwtSecret,
      { expiresIn: config.jwtExpiresIn }
    );
    
    // Refresh токен (длинный срок жизни)
    const refreshPayload: LifeVaultJwtPayload = {
      ...payload,
      type: 'refresh'
    };
    
    const refreshToken = jwt.sign(
      refreshPayload,
      config.jwtSecret,
      { expiresIn: '30d' }
    );
    
    // Сохраняем refresh токен в базе
    await prisma.refreshToken.create({
      data: {
        token: refreshToken,
        userId: user.id,
        expiresAt: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 дней
      },
    });
    
    return { accessToken, refreshToken };
  }
  
  // ✅ Верификация access токена
  verifyAccessToken(token: string): LifeVaultJwtPayload | null {
    try {
      const decoded = jwt.verify(token, config.jwtSecret) as LifeVaultJwtPayload;
      
      if (decoded.type !== 'access') {
        return null;
      }
      
      return decoded;
    } catch {
      return null;
    }
  }
  
  // 🧹 Очистка истекших токенов
  async cleanupExpiredTokens(): Promise<number> {
    const result = await prisma.refreshToken.deleteMany({
      where: {
        expiresAt: {
          lt: new Date()
        }
      }
    });
    
    return result.count;
  }
  
  // 📱 Нормализация номера телефона
  private normalizePhoneNumber(phoneNumber: string): string {
    // Убираем все символы кроме цифр
    const digits = phoneNumber.replace(/\D/g, '');
    
    // Добавляем + если номер начинается с кода страны
    if (digits.length >= 10) {
      return '+' + digits;
    }
    
    return digits;
  }
  
  // 🔢 Генерация кода верификации
  private generateVerificationCode(): string {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }
  
  // 👤 Получение информации о текущем пользователе
  async getCurrentUser(userId: string) {
    return await prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        phoneNumber: true,
        isPhoneVerified: true,
        createdAt: true,
        lastLoginAt: true,
        profile: true,
      },
    });
  }
  
  // 🗑️ Удаление аккаунта пользователя
  async deleteAccount(userId: string): Promise<{ success: boolean; message: string }> {
    try {
      await prisma.user.delete({
        where: { id: userId }
      });
      
      return {
        success: true,
        message: 'Аккаунт успешно удален'
      };
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/delete-account');
      return {
        success: false,
        message: 'Ошибка при удалении аккаунта'
      };
    }
  }
}

export const authService = new AuthService();