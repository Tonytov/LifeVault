// 📝 Система логгирования для LifeVault

import winston from 'winston';
import { config, isDevelopment } from './config';

// 🎨 Цветная палитра для консоли
const colors = {
  error: 'red',
  warn: 'yellow',
  info: 'cyan',
  debug: 'green',
  http: 'magenta',
};

winston.addColors(colors);

// 📊 Форматы логов
const consoleFormat = winston.format.combine(
  winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss:ms' }),
  winston.format.colorize({ all: true }),
  winston.format.printf((info) => {
    const { timestamp, level, message, ...args } = info;
    const ts = timestamp.slice(0, 19).replace('T', ' ');
    return `${ts} [${level}]: ${message} ${Object.keys(args).length ? JSON.stringify(args, null, 2) : ''}`;
  })
);

const fileFormat = winston.format.combine(
  winston.format.timestamp(),
  winston.format.errors({ stack: true }),
  winston.format.json()
);

// 🎯 Транспорты (куда логгируем)
const transports: winston.transport[] = [];

// Консольный вывод для разработки
if (isDevelopment()) {
  transports.push(
    new winston.transports.Console({
      format: consoleFormat,
      level: 'debug',
    })
  );
} else {
  // Продакшн логи
  transports.push(
    new winston.transports.Console({
      format: winston.format.simple(),
      level: 'info',
    }),
    new winston.transports.File({
      filename: 'logs/error.log',
      level: 'error',
      format: fileFormat,
    }),
    new winston.transports.File({
      filename: 'logs/combined.log',
      format: fileFormat,
    })
  );
}

// 🏗️ Создание логгера
export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || (isDevelopment() ? 'debug' : 'info'),
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.metadata()
  ),
  transports,
  exceptionHandlers: [
    new winston.transports.File({ filename: 'logs/exceptions.log' })
  ],
  rejectionHandlers: [
    new winston.transports.File({ filename: 'logs/rejections.log' })
  ],
});

// 🚀 Специальные методы логгирования для LifeVault
export const appLogger = {
  // 👤 Аутентификация
  auth: {
    login: (phoneNumber: string, success: boolean) => {
      logger.info('User login attempt', { 
        phoneNumber: phoneNumber.slice(-4), // только последние 4 цифры
        success,
        action: 'login'
      });
    },
    register: (phoneNumber: string) => {
      logger.info('User registration', { 
        phoneNumber: phoneNumber.slice(-4),
        action: 'register'
      });
    },
    verify: (phoneNumber: string, success: boolean) => {
      logger.info('Phone verification', { 
        phoneNumber: phoneNumber.slice(-4),
        success,
        action: 'verify'
      });
    }
  },

  // 🎯 Вызовы
  challenge: {
    start: (userId: string, challengeId: string) => {
      logger.info('Challenge started', { userId, challengeId, action: 'challenge_start' });
    },
    complete: (userId: string, challengeId: string) => {
      logger.info('Challenge completed', { userId, challengeId, action: 'challenge_complete' });
    },
    fail: (userId: string, challengeId: string, reason?: string) => {
      logger.warn('Challenge failed', { userId, challengeId, reason, action: 'challenge_fail' });
    }
  },

  // 🚭 Привычки
  habit: {
    create: (userId: string, habitType: string) => {
      logger.info('Habit created', { userId, habitType, action: 'habit_create' });
    },
    quit: (userId: string, habitType: string) => {
      logger.info('Habit quit', { userId, habitType, action: 'habit_quit' });
    },
    resume: (userId: string, habitType: string) => {
      logger.warn('Habit resumed', { userId, habitType, action: 'habit_resume' });
    }
  },

  // 🏆 Достижения
  achievement: {
    unlock: (userId: string, achievementId: string) => {
      logger.info('Achievement unlocked', { userId, achievementId, action: 'achievement_unlock' });
    }
  },

  // 💳 Банк времени
  timebank: {
    transaction: (userId: string, type: string, amount: number) => {
      logger.info('TimeBank transaction', { userId, type, amount, action: 'timebank_transaction' });
    }
  },

  // 🔧 Система
  system: {
    startup: () => {
      logger.info('🚀 LifeVault Backend started successfully');
    },
    shutdown: () => {
      logger.info('🛑 LifeVault Backend shutting down');
    },
    dbConnect: () => {
      logger.info('🗄️ Database connected successfully');
    },
    dbDisconnect: () => {
      logger.info('🗄️ Database disconnected');
    }
  },

  // ⚠️ Ошибки
  error: {
    api: (error: Error, endpoint: string, userId?: string) => {
      logger.error('API Error', { 
        message: error.message, 
        stack: error.stack,
        endpoint,
        userId
      });
    },
    database: (error: Error, operation: string) => {
      logger.error('Database Error', { 
        message: error.message, 
        stack: error.stack,
        operation
      });
    },
    external: (service: string, error: Error) => {
      logger.error('External Service Error', { 
        service,
        message: error.message, 
        stack: error.stack
      });
    }
  }
};

// 🔍 Middleware для логгирования HTTP запросов
export const httpLogger = winston.createLogger({
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.printf(({ timestamp, level, message }) => {
      return `${timestamp} [${level}]: ${message}`;
    })
  ),
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      )
    })
  ]
});