// 🔐 Контроллер аутентификации LifeVault

import { Request, Response } from 'express';
import { authService } from '@/services/auth.service';
import { appLogger } from '@/utils/logger';
import type { AuthenticatedRequest, SMSVerificationRequest } from '@/types';

export class AuthController {
  
  // 📱 Отправка SMS кода
  async sendVerificationCode(req: Request, res: Response): Promise<void> {
    try {
      const { phoneNumber } = req.body;
      
      const result = await authService.sendVerificationCode(phoneNumber);
      
      if (result.success) {
        res.status(200).json({
          success: true,
          message: result.message
        });
      } else {
        res.status(400).json({
          success: false,
          message: result.message
        });
      }
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/send-code');
      res.status(500).json({
        success: false,
        message: 'Ошибка при отправке кода верификации'
      });
    }
  }
  
  // ✅ Верификация SMS кода
  async verifyCode(req: Request, res: Response): Promise<void> {
    try {
      const verificationData: SMSVerificationRequest = req.body;
      
      const result = await authService.verifyCode(verificationData);
      
      if (result.success) {
        res.status(200).json({
          success: true,
          message: result.message,
          data: {
            token: result.token,
            refreshToken: result.refreshToken
          }
        });
      } else {
        res.status(400).json({
          success: false,
          message: result.message
        });
      }
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/verify-code');
      res.status(500).json({
        success: false,
        message: 'Ошибка при верификации кода'
      });
    }
  }
  
  // 🔄 Обновление токена
  async refreshToken(req: Request, res: Response): Promise<void> {
    try {
      const { refreshToken } = req.body;
      
      const result = await authService.refreshAccessToken(refreshToken);
      
      if (result.success) {
        res.status(200).json({
          success: true,
          message: result.message,
          data: {
            token: result.token,
            refreshToken: result.refreshToken
          }
        });
      } else {
        res.status(401).json({
          success: false,
          message: result.message
        });
      }
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/refresh');
      res.status(500).json({
        success: false,
        message: 'Ошибка при обновлении токена'
      });
    }
  }
  
  // 🚪 Выход из системы
  async logout(req: Request, res: Response): Promise<void> {
    try {
      const { refreshToken } = req.body;
      
      const result = await authService.logout(refreshToken);
      
      res.status(200).json({
        success: result.success,
        message: result.message
      });
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/logout');
      res.status(500).json({
        success: false,
        message: 'Ошибка при выходе из системы'
      });
    }
  }
  
  // 👤 Получение информации о текущем пользователе
  async getCurrentUser(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      if (!req.user) {
        res.status(401).json({
          success: false,
          message: 'Пользователь не аутентифицирован'
        });
        return;
      }
      
      const user = await authService.getCurrentUser(req.user.id);
      
      if (!user) {
        res.status(404).json({
          success: false,
          message: 'Пользователь не найден'
        });
        return;
      }
      
      res.status(200).json({
        success: true,
        data: {
          id: user.id,
          phoneNumber: user.phoneNumber,
          isPhoneVerified: user.isPhoneVerified,
          hasProfile: !!user.profile,
          profile: user.profile ? {
            firstName: user.profile.firstName,
            lastName: user.profile.lastName,
            gender: user.profile.gender,
            region: user.profile.region
          } : null,
          createdAt: user.createdAt,
          lastLoginAt: user.lastLoginAt
        }
      });
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/me', req.user?.id);
      res.status(500).json({
        success: false,
        message: 'Ошибка при получении информации о пользователе'
      });
    }
  }
  
  // 🔍 Проверка статуса аутентификации
  async checkAuthStatus(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const isAuthenticated = !!req.user;
      
      res.status(200).json({
        success: true,
        data: {
          isAuthenticated,
          user: isAuthenticated ? {
            id: req.user!.id,
            phoneNumber: req.user!.phoneNumber,
            isPhoneVerified: req.user!.isPhoneVerified
          } : null
        }
      });
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/status', req.user?.id);
      res.status(500).json({
        success: false,
        message: 'Ошибка при проверке статуса аутентификации'
      });
    }
  }
  
  // 🗑️ Удаление аккаунта
  async deleteAccount(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      if (!req.user) {
        res.status(401).json({
          success: false,
          message: 'Пользователь не аутентифицирован'
        });
        return;
      }
      
      const result = await authService.deleteAccount(req.user.id);
      
      if (result.success) {
        appLogger.auth.login(req.user.phoneNumber, false); // Логируем удаление аккаунта
        
        res.status(200).json({
          success: true,
          message: result.message
        });
      } else {
        res.status(400).json({
          success: false,
          message: result.message
        });
      }
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/delete-account', req.user?.id);
      res.status(500).json({
        success: false,
        message: 'Ошибка при удалении аккаунта'
      });
    }
  }
  
  // 🧹 Очистка истекших токенов (административная функция)
  async cleanupTokens(req: Request, res: Response): Promise<void> {
    try {
      // Простая проверка административного доступа
      const adminKey = req.headers['x-admin-key'];
      if (!adminKey || adminKey !== process.env.ADMIN_KEY) {
        res.status(403).json({
          success: false,
          message: 'Недостаточно прав доступа'
        });
        return;
      }
      
      const deletedCount = await authService.cleanupExpiredTokens();
      
      res.status(200).json({
        success: true,
        message: `Очищено ${deletedCount} истекших токенов`
      });
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/cleanup-tokens');
      res.status(500).json({
        success: false,
        message: 'Ошибка при очистке токенов'
      });
    }
  }
  
  // 📊 Статистика аутентификации (для мониторинга)
  async getAuthStats(req: Request, res: Response): Promise<void> {
    try {
      // Простая проверка административного доступа
      const adminKey = req.headers['x-admin-key'];
      if (!adminKey || adminKey !== process.env.ADMIN_KEY) {
        res.status(403).json({
          success: false,
          message: 'Недостаточно прав доступа'
        });
        return;
      }
      
      // Здесь можно добавить более подробную статистику
      // Пока возвращаем базовую информацию
      res.status(200).json({
        success: true,
        data: {
          message: 'Статистика аутентификации доступна',
          timestamp: new Date().toISOString()
        }
      });
      
    } catch (error) {
      appLogger.error.api(error as Error, '/auth/stats');
      res.status(500).json({
        success: false,
        message: 'Ошибка при получении статистики'
      });
    }
  }
}

export const authController = new AuthController();