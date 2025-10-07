// 🔐 Middleware для аутентификации LifeVault

import { Request, Response, NextFunction } from 'express';
import { authService } from '@/services/auth.service';
import { appLogger } from '@/utils/logger';
import type { AuthenticatedRequest } from '@/types';

// 🛡️ Проверка JWT токена
export const authenticateToken = async (
  req: AuthenticatedRequest, 
  res: Response, 
  next: NextFunction
): Promise<void> => {
  try {
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN
    
    if (!token) {
      res.status(401).json({
        success: false,
        message: 'Токен доступа не предоставлен'
      });
      return;
    }
    
    // Верифицируем токен
    const decoded = authService.verifyAccessToken(token);
    
    if (!decoded) {
      res.status(403).json({
        success: false,
        message: 'Недействительный токен доступа'
      });
      return;
    }
    
    // Проверяем, существует ли пользователь
    const user = await authService.getCurrentUser(decoded.userId);
    
    if (!user) {
      res.status(403).json({
        success: false,
        message: 'Пользователь не найден'
      });
      return;
    }
    
    if (!user.isPhoneVerified) {
      res.status(403).json({
        success: false,
        message: 'Телефон не верифицирован'
      });
      return;
    }
    
    // Добавляем пользователя в запрос
    req.user = {
      id: user.id,
      phoneNumber: user.phoneNumber,
      isPhoneVerified: user.isPhoneVerified
    };
    
    next();
    
  } catch (error) {
    appLogger.error.api(error as Error, req.path, req.user?.id);
    res.status(500).json({
      success: false,
      message: 'Ошибка при проверке токена'
    });
  }
};

// 📱 Опциональная аутентификация (для публичных endpoint'ов с дополнительной функциональностью для авторизованных)
export const optionalAuth = async (
  req: AuthenticatedRequest, 
  res: Response, 
  next: NextFunction
): Promise<void> => {
  try {
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1];
    
    if (token) {
      const decoded = authService.verifyAccessToken(token);
      
      if (decoded) {
        const user = await authService.getCurrentUser(decoded.userId);
        
        if (user && user.isPhoneVerified) {
          req.user = {
            id: user.id,
            phoneNumber: user.phoneNumber,
            isPhoneVerified: user.isPhoneVerified
          };
        }
      }
    }
    
    next();
    
  } catch (error) {
    // При ошибках в опциональной аутентификации просто продолжаем без пользователя
    next();
  }
};

// 👤 Проверка существования профиля пользователя
export const requireProfile = async (
  req: AuthenticatedRequest, 
  res: Response, 
  next: NextFunction
): Promise<void> => {
  try {
    if (!req.user) {
      res.status(401).json({
        success: false,
        message: 'Требуется аутентификация'
      });
      return;
    }
    
    const user = await authService.getCurrentUser(req.user.id);
    
    if (!user?.profile) {
      res.status(400).json({
        success: false,
        message: 'Требуется заполнить профиль пользователя'
      });
      return;
    }
    
    next();
    
  } catch (error) {
    appLogger.error.api(error as Error, req.path, req.user?.id);
    res.status(500).json({
      success: false,
      message: 'Ошибка при проверке профиля'
    });
  }
};

// 🔒 Middleware для защиты от CSRF атак (простая проверка Origin)
export const csrfProtection = (req: Request, res: Response, next: NextFunction): void => {
  const allowedOrigins = process.env.CORS_ORIGIN?.split(',') || ['http://localhost:3000'];
  const origin = req.headers.origin;
  
  // Разрешаем запросы без origin (например, с мобильного приложения)
  if (!origin) {
    next();
    return;
  }
  
  if (allowedOrigins.includes(origin)) {
    next();
    return;
  }
  
  res.status(403).json({
    success: false,
    message: 'Запрос заблокирован: недопустимый origin'
  });
};

// 🔄 Middleware для логгирования запросов
export const requestLogger = (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
  const startTime = Date.now();
  
  res.on('finish', () => {
    const duration = Date.now() - startTime;
    const userId = req.user?.id || 'anonymous';
    
    appLogger.system.startup(); // Используем существующий метод, можно добавить отдельный
    
    if (process.env.ENABLE_REQUEST_LOGGING === 'true') {
      console.log(`📝 ${req.method} ${req.path} - ${res.statusCode} - ${duration}ms - User: ${userId}`);
    }
  });
  
  next();
};