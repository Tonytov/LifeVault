// 🔧 Конфигурация приложения LifeVault

import dotenv from 'dotenv';
import { AppConfig } from '@/types';

// Загружаем переменные окружения
dotenv.config();

// Валидация обязательных переменных
const requiredEnvVars = [
  'DATABASE_URL',
  'JWT_SECRET',
  'TWILIO_ACCOUNT_SID',
  'TWILIO_AUTH_TOKEN',
  'TWILIO_PHONE_NUMBER'
];

for (const envVar of requiredEnvVars) {
  if (!process.env[envVar]) {
    throw new Error(`🔥 Отсутствует обязательная переменная окружения: ${envVar}`);
  }
}

// Основная конфигурация
export const config: AppConfig = {
  // 🌐 Сервер
  port: parseInt(process.env.PORT || '3000', 10),
  nodeEnv: process.env.NODE_ENV || 'development',
  
  // 🗄️ База данных
  databaseUrl: process.env.DATABASE_URL!,
  
  // 🔐 Аутентификация
  jwtSecret: process.env.JWT_SECRET!,
  jwtExpiresIn: process.env.JWT_EXPIRES_IN || '7d',
  bcryptRounds: parseInt(process.env.BCRYPT_ROUNDS || '12', 10),
  
  // 📱 SMS сервис
  twilioAccountSid: process.env.TWILIO_ACCOUNT_SID!,
  twilioAuthToken: process.env.TWILIO_AUTH_TOKEN!,
  twilioPhoneNumber: process.env.TWILIO_PHONE_NUMBER!,
  
  // 🌍 CORS
  corsOrigin: process.env.CORS_ORIGIN || 'http://localhost:3000',
  
  // 🔒 Безопасность
  rateLimitWindowMs: parseInt(process.env.API_RATE_LIMIT_WINDOW_MS || '900000', 10),
  rateLimitMax: parseInt(process.env.API_RATE_LIMIT_MAX || '100', 10),
};

// 🧪 Конфигурация для тестов
export const testConfig = {
  ...config,
  databaseUrl: process.env.TEST_DATABASE_URL || config.databaseUrl,
  jwtSecret: 'test-secret-key',
  nodeEnv: 'test',
};

// 🔍 Логгирование конфигурации (без секретов)
export const logSafeConfig = () => {
  const safeConfig = {
    port: config.port,
    nodeEnv: config.nodeEnv,
    corsOrigin: config.corsOrigin,
    rateLimitWindowMs: config.rateLimitWindowMs,
    rateLimitMax: config.rateLimitMax,
    bcryptRounds: config.bcryptRounds,
  };
  
  console.log('🔧 Конфигурация приложения:', safeConfig);
};

// 🌱 Проверка окружения
export const isDevelopment = () => config.nodeEnv === 'development';
export const isProduction = () => config.nodeEnv === 'production';
export const isTest = () => config.nodeEnv === 'test';