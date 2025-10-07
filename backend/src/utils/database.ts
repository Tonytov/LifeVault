// 🗄️ Подключение к базе данных LifeVault

import { PrismaClient } from '@prisma/client';
import { appLogger } from './logger';
import { config, isDevelopment } from './config';

// 🔧 Создание Prisma клиента с настройками
export const prisma = new PrismaClient({
  log: isDevelopment() 
    ? ['query', 'info', 'warn', 'error'] 
    : ['info', 'warn', 'error'],
  
  errorFormat: 'pretty',
  
  datasources: {
    db: {
      url: config.databaseUrl,
    },
  },
});

// 🔌 Подключение к базе данных
export const connectDatabase = async (): Promise<void> => {
  try {
    await prisma.$connect();
    appLogger.system.dbConnect();
    
    // 🧪 Тест подключения
    await prisma.$queryRaw`SELECT 1`;
    
    if (isDevelopment()) {
      console.log('🟢 База данных успешно подключена');
    }
  } catch (error) {
    appLogger.error.database(error as Error, 'connection');
    console.error('🔴 Ошибка подключения к базе данных:', error);
    process.exit(1);
  }
};

// 🔌 Отключение от базы данных
export const disconnectDatabase = async (): Promise<void> => {
  try {
    await prisma.$disconnect();
    appLogger.system.dbDisconnect();
  } catch (error) {
    appLogger.error.database(error as Error, 'disconnection');
    console.error('Ошибка при отключении от базы данных:', error);
  }
};

// 🔄 Миграции и сидирование в разработке
export const setupDatabase = async (): Promise<void> => {
  if (!isDevelopment()) return;
  
  try {
    // Проверяем, нужны ли миграции
    const pendingMigrations = await prisma.$queryRaw`
      SELECT COUNT(*) as count 
      FROM information_schema.tables 
      WHERE table_schema = 'public' 
      AND table_name = '_prisma_migrations'
    `;
    
    console.log('📊 Состояние миграций:', pendingMigrations);
  } catch (error) {
    console.warn('⚠️ Не удалось проверить состояние миграций:', error);
  }
};

// 🧹 Функция для очистки данных (только для тестов)
export const cleanDatabase = async (): Promise<void> => {
  if (config.nodeEnv !== 'test') {
    throw new Error('🚫 Очистка базы данных разрешена только в тестовом режиме!');
  }
  
  // Порядок удаления важен из-за foreign key constraints
  await prisma.pushNotification.deleteMany();
  await prisma.userAchievement.deleteMany();
  await prisma.timeBankTransaction.deleteMany();
  await prisma.timeBankAccount.deleteMany();
  await prisma.userChallengeProgress.deleteMany();
  await prisma.habitLog.deleteMany();
  await prisma.habit.deleteMany();
  await prisma.userProfile.deleteMany();
  await prisma.refreshToken.deleteMany();
  await prisma.user.deleteMany();
  await prisma.challenge.deleteMany();
  await prisma.achievement.deleteMany();
  await prisma.storeItem.deleteMany();
};

// 📊 Функции для работы с данными
export const databaseUtils = {
  // 📈 Статистика базы данных
  getStats: async () => {
    const [
      totalUsers,
      verifiedUsers, 
      totalHabits,
      activeHabits,
      totalChallenges,
      activeChallenges,
      totalAchievements
    ] = await Promise.all([
      prisma.user.count(),
      prisma.user.count({ where: { isPhoneVerified: true } }),
      prisma.habit.count(),
      prisma.habit.count({ where: { isActive: true } }),
      prisma.challenge.count(),
      prisma.userChallengeProgress.count({ where: { status: 'IN_PROGRESS' } }),
      prisma.achievement.count()
    ]);

    return {
      users: {
        total: totalUsers,
        verified: verifiedUsers,
        verificationRate: totalUsers > 0 ? (verifiedUsers / totalUsers) * 100 : 0
      },
      habits: {
        total: totalHabits,
        active: activeHabits,
        quitRate: totalHabits > 0 ? ((totalHabits - activeHabits) / totalHabits) * 100 : 0
      },
      challenges: {
        total: totalChallenges,
        active: activeChallenges
      },
      achievements: {
        total: totalAchievements
      }
    };
  },

  // 🔍 Поиск пользователя безопасно
  findUserSafely: async (phoneNumber: string) => {
    return await prisma.user.findUnique({
      where: { phoneNumber },
      select: {
        id: true,
        phoneNumber: true,
        isPhoneVerified: true,
        createdAt: true,
        lastLoginAt: true,
        profile: {
          select: {
            firstName: true,
            lastName: true,
            gender: true,
            region: true
          }
        }
      }
    });
  },

  // 🧼 Очистка истекших токенов
  cleanupExpiredTokens: async () => {
    const result = await prisma.refreshToken.deleteMany({
      where: {
        expiresAt: {
          lt: new Date()
        }
      }
    });
    
    if (result.count > 0) {
      appLogger.system.startup();
      console.log(`🧹 Удалено ${result.count} истекших токенов`);
    }
    
    return result.count;
  }
};

// 🔄 Graceful shutdown для базы данных
process.on('beforeExit', async () => {
  await disconnectDatabase();
});

process.on('SIGINT', async () => {
  await disconnectDatabase();
  process.exit(0);
});

process.on('SIGTERM', async () => {
  await disconnectDatabase();
  process.exit(0);
});