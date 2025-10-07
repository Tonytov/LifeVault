// 🚀 LifeVault Backend Server

import express, { Application, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import rateLimit from 'express-rate-limit';

import { config, logSafeConfig, isDevelopment } from '@/utils/config';
import { appLogger } from '@/utils/logger';
import { connectDatabase, disconnectDatabase, databaseUtils } from '@/utils/database';
import { requestLogger } from '@/middleware/auth.middleware';

// 📁 Импорт роутов
import authRoutes from '@/routes/auth.routes';
// import userRoutes from '@/routes/user.routes';
// import habitRoutes from '@/routes/habit.routes';
// import challengeRoutes from '@/routes/challenge.routes';
// import timebankRoutes from '@/routes/timebank.routes';

class LifeVaultServer {
  private app: Application;
  private port: number;

  constructor() {
    this.app = express();
    this.port = config.port;
    this.setupMiddleware();
    this.setupRoutes();
    this.setupErrorHandling();
  }

  // 🔧 Настройка middleware
  private setupMiddleware(): void {
    // 🛡️ Безопасность
    this.app.use(helmet({
      contentSecurityPolicy: {
        directives: {
          defaultSrc: ["'self'"],
          styleSrc: ["'self'", "'unsafe-inline'"],
          scriptSrc: ["'self'"],
          imgSrc: ["'self'", "data:", "https:"],
        },
      },
    }));

    // 🌍 CORS
    this.app.use(cors({
      origin: config.corsOrigin.split(','),
      credentials: true,
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
    }));

    // 📦 Сжатие
    this.app.use(compression());

    // 📊 Логгирование запросов
    if (isDevelopment()) {
      this.app.use(requestLogger);
    }

    // 🔢 Парсинг JSON
    this.app.use(express.json({ 
      limit: '10mb',
      verify: (req: any, res, buf, encoding) => {
        // Проверка размера тела запроса
        if (buf.length > 10 * 1024 * 1024) { // 10MB
          throw new Error('Тело запроса слишком большое');
        }
      }
    }));
    
    this.app.use(express.urlencoded({ extended: true, limit: '10mb' }));

    // 🔒 Rate limiting
    const limiter = rateLimit({
      windowMs: config.rateLimitWindowMs,
      max: config.rateLimitMax,
      message: {
        success: false,
        message: 'Слишком много запросов, попробуйте позже'
      },
      standardHeaders: true,
      legacyHeaders: false,
    });
    
    this.app.use('/api/', limiter);

    // 📊 Более строгий лимит для аутентификации
    const authLimiter = rateLimit({
      windowMs: 15 * 60 * 1000, // 15 минут
      max: 5, // максимум 5 попыток входа за 15 минут
      message: {
        success: false,
        message: 'Слишком много попыток входа, попробуйте позже'
      }
    });
    
    this.app.use('/api/auth/send-code', authLimiter);
    this.app.use('/api/auth/verify-code', authLimiter);
  }

  // 🛣️ Настройка маршрутов
  private setupRoutes(): void {
    // 💚 Health check
    this.app.get('/health', async (req: Request, res: Response) => {
      try {
        // Проверяем подключение к базе данных
        const dbStats = await databaseUtils.getStats();
        
        res.json({
          success: true,
          message: 'LifeVault Backend работает нормально',
          timestamp: new Date().toISOString(),
          version: '1.0.0',
          environment: config.nodeEnv,
          database: {
            connected: true,
            stats: dbStats
          },
          uptime: Math.floor(process.uptime())
        });
      } catch (error) {
        res.status(503).json({
          success: false,
          message: 'Проблемы со здоровьем сервиса',
          error: isDevelopment() ? (error as Error).message : undefined
        });
      }
    });

    // 📊 Статистика сервера (для мониторинга)
    this.app.get('/metrics', async (req: Request, res: Response) => {
      try {
        const memoryUsage = process.memoryUsage();
        const uptime = process.uptime();
        const dbStats = await databaseUtils.getStats();

        res.json({
          success: true,
          data: {
            server: {
              uptime: Math.floor(uptime),
              memoryUsage: {
                rss: Math.round(memoryUsage.rss / 1024 / 1024) + ' MB',
                heapTotal: Math.round(memoryUsage.heapTotal / 1024 / 1024) + ' MB',
                heapUsed: Math.round(memoryUsage.heapUsed / 1024 / 1024) + ' MB'
              },
              nodeVersion: process.version,
              platform: process.platform
            },
            database: dbStats
          }
        });
      } catch (error) {
        res.status(500).json({
          success: false,
          message: 'Ошибка получения метрик'
        });
      }
    });

    // 🏠 Корневой маршрут
    this.app.get('/', (req: Request, res: Response) => {
      res.json({
        success: true,
        message: '🔒 LifeVault API v1.0',
        description: 'Backend для отслеживания привычек и продолжительности жизни',
        documentation: '/api-docs',
        endpoints: {
          health: '/health',
          metrics: '/metrics',
          auth: '/api/auth/*',
          user: '/api/user/*',
          habits: '/api/habits/*',
          challenges: '/api/challenges/*',
          timebank: '/api/timebank/*'
        }
      });
    });

    // 🔌 API роуты
    const apiRouter = express.Router();
    
    // Временные заглушки для роутов
    apiRouter.get('/auth/status', (req: Request, res: Response) => {
      res.json({ success: true, message: 'Auth service ready' });
    });
    
    apiRouter.get('/user/profile', (req: Request, res: Response) => {
      res.json({ success: true, message: 'User service ready' });
    });
    
    apiRouter.get('/habits', (req: Request, res: Response) => {
      res.json({ success: true, message: 'Habits service ready' });
    });
    
    apiRouter.get('/challenges', (req: Request, res: Response) => {
      res.json({ success: true, message: 'Challenges service ready' });
    });
    
    apiRouter.get('/timebank', (req: Request, res: Response) => {
      res.json({ success: true, message: 'TimeBank service ready' });
    });

    // Подключение роутов
    apiRouter.use('/auth', authRoutes);
    // TODO: Подключить остальные роуты
    // apiRouter.use('/user', userRoutes);
    // apiRouter.use('/habits', habitRoutes);
    // apiRouter.use('/challenges', challengeRoutes);
    // apiRouter.use('/timebank', timebankRoutes);

    this.app.use('/api', apiRouter);

    // 🔍 404 обработчик
    this.app.all('*', (req: Request, res: Response) => {
      res.status(404).json({
        success: false,
        message: `Маршрут ${req.method} ${req.path} не найден`,
        availableEndpoints: [
          '/',
          '/health', 
          '/metrics',
          '/api/auth/*',
          '/api/user/*',
          '/api/habits/*',
          '/api/challenges/*',
          '/api/timebank/*'
        ]
      });
    });
  }

  // ⚠️ Обработка ошибок
  private setupErrorHandling(): void {
    // Глобальный обработчик ошибок
    this.app.use((error: Error, req: Request, res: Response, next: NextFunction) => {
      appLogger.error.api(error, req.path);

      // Проверяем тип ошибки
      if (error.name === 'ValidationError') {
        res.status(400).json({
          success: false,
          message: 'Ошибка валидации данных',
          error: isDevelopment() ? error.message : undefined
        });
        return;
      }

      if (error.name === 'UnauthorizedError') {
        res.status(401).json({
          success: false,
          message: 'Требуется аутентификация'
        });
        return;
      }

      if (error.message.includes('ENOTFOUND') || error.message.includes('ECONNREFUSED')) {
        res.status(503).json({
          success: false,
          message: 'Проблемы с внешними сервисами'
        });
        return;
      }

      // Общая ошибка сервера
      res.status(500).json({
        success: false,
        message: 'Внутренняя ошибка сервера',
        error: isDevelopment() ? error.message : undefined,
        stack: isDevelopment() ? error.stack : undefined
      });
    });

    // Обработчики неперехваченных ошибок
    process.on('uncaughtException', (error: Error) => {
      appLogger.error.api(error, 'uncaughtException');
      console.error('💥 Неперехваченная ошибка:', error);
      this.gracefulShutdown('SIGTERM');
    });

    process.on('unhandledRejection', (reason: unknown, promise: Promise<any>) => {
      appLogger.error.api(new Error(`Unhandled Rejection: ${reason}`), 'unhandledRejection');
      console.error('💥 Необработанное отклонение промиса:', promise, 'причина:', reason);
    });
  }

  // 🚀 Запуск сервера
  public async start(): Promise<void> {
    try {
      // Подключаемся к базе данных
      await connectDatabase();

      // Запускаем сервер
      this.app.listen(this.port, () => {
        appLogger.system.startup();
        logSafeConfig();
        
        console.log(`
🔒 LifeVault Backend успешно запущен!

🌐 Сервер:     http://localhost:${this.port}
🔧 Окружение:  ${config.nodeEnv}
💚 Здоровье:   http://localhost:${this.port}/health
📊 Метрики:    http://localhost:${this.port}/metrics
📚 API:        http://localhost:${this.port}/api

🔥 Готов к работе!
        `);
      });

      // Настраиваем graceful shutdown
      this.setupGracefulShutdown();

    } catch (error) {
      console.error('💥 Ошибка запуска сервера:', error);
      process.exit(1);
    }
  }

  // 🛑 Graceful shutdown
  private setupGracefulShutdown(): void {
    const signals = ['SIGTERM', 'SIGINT', 'SIGUSR2'];
    
    signals.forEach((signal) => {
      process.on(signal, () => {
        console.log(`\n🛑 Получен сигнал ${signal}. Начинаем graceful shutdown...`);
        this.gracefulShutdown(signal);
      });
    });
  }

  private async gracefulShutdown(signal: string): Promise<void> {
    console.log(`🔄 Завершение работы по сигналу ${signal}...`);
    
    try {
      // Закрываем подключение к базе данных
      await disconnectDatabase();
      appLogger.system.shutdown();
      
      console.log('✅ Graceful shutdown завершен');
      process.exit(0);
    } catch (error) {
      console.error('💥 Ошибка при graceful shutdown:', error);
      process.exit(1);
    }
  }
}

// 🚀 Запуск приложения
if (require.main === module) {
  const server = new LifeVaultServer();
  server.start().catch((error) => {
    console.error('💥 Критическая ошибка запуска:', error);
    process.exit(1);
  });
}

export default LifeVaultServer;