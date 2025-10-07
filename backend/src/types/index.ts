// 🔧 Основные типы для LifeVault Backend

import { Request } from 'express';
import { JwtPayload } from 'jsonwebtoken';

// 👤 Аутентифицированный пользователь в запросе
export interface AuthenticatedRequest extends Request {
  user?: {
    id: string;
    phoneNumber: string;
    isPhoneVerified: boolean;
  };
}

// 🔐 JWT Payload
export interface LifeVaultJwtPayload extends JwtPayload {
  userId: string;
  phoneNumber: string;
  type: 'access' | 'refresh';
}

// 📱 SMS верификация
export interface SMSVerificationRequest {
  phoneNumber: string;
  code: string;
}

export interface SMSVerificationResponse {
  success: boolean;
  message: string;
  token?: string;
  refreshToken?: string;
}

// 👤 Профиль пользователя
export interface UserProfileData {
  firstName?: string;
  lastName?: string;
  dateOfBirth: Date;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  height: number; // см
  weight: number; // кг
  region: string; // ISO код
}

// 🚭 Привычки
export interface HabitData {
  type: 'SMOKING' | 'ALCOHOL' | 'SUGAR';
  isActive: boolean;
  dailyUsage?: number;
  costPerUnit?: number;
  unitsPerPack?: number;
  startDate?: Date;
  quitDate?: Date;
  targetQuitDate?: Date;
  additionalInfo?: string;
}

export interface HabitCalculation {
  totalDaysWithoutHabit: number;
  unitsNotConsumed: number;
  moneySaved: number;
  healthImprovements: string[];
  lifeExtensionDays: number;
}

// 🎯 Вызовы
export interface ChallengeData {
  id: string;
  title: string;
  description: string;
  category: ChallengeCategory;
  difficulty: ChallengeDifficulty;
  duration: ChallengeDuration;
  rewardTimeBank: number;
}

export type ChallengeCategory = 
  | 'QUIT_HABITS' 
  | 'HEALTH_IMPROVEMENT' 
  | 'MENTAL_WELLNESS' 
  | 'FITNESS' 
  | 'NUTRITION';

export type ChallengeDifficulty = 
  | 'BEGINNER' 
  | 'INTERMEDIATE' 
  | 'ADVANCED' 
  | 'EXPERT';

export type ChallengeDuration = 
  | 'ONE_DAY' 
  | 'THREE_DAYS' 
  | 'ONE_WEEK' 
  | 'TWO_WEEKS' 
  | 'ONE_MONTH' 
  | 'THREE_MONTHS' 
  | 'SIX_MONTHS' 
  | 'ONE_YEAR';

// 📊 Статистика пользователя
export interface UserStatsResponse {
  totalChallengesStarted: number;
  totalChallengesCompleted: number;
  completionRate: number;
  longestStreak: number;
  currentStreak: number;
  totalRewardsEarned: number;
  
  // Привычки
  daysSmokeFree: number;
  cigarettesNotSmoked: number;
  moneySavedTotal: number;
  
  // Продолжительность жизни
  baseLifeExpectancy: number;
  currentLifeExpectancy: number;
  lifeExtensionDays: number;
}

// 🏆 Достижения
export interface AchievementData {
  id: string;
  name: string;
  description: string;
  category: AchievementCategory;
  iconEmoji: string;
  rewardTimeBank: number;
  rarity: AchievementRarity;
  unlockedAt?: Date;
}

export type AchievementCategory = 
  | 'HABITS' 
  | 'CHALLENGES' 
  | 'TIME_SAVED' 
  | 'SOCIAL' 
  | 'MILESTONES';

export type AchievementRarity = 
  | 'COMMON' 
  | 'RARE' 
  | 'EPIC' 
  | 'LEGENDARY';

// 💳 Банк времени
export interface TimeBankBalance {
  totalDaysEarned: number;
  totalDaysSpent: number;
  currentBalance: number;
}

export interface TimeBankTransactionData {
  type: 'EARNED' | 'SPENT' | 'BONUS' | 'REFUND';
  amount: number;
  description: string;
  relatedEntity?: string;
  relatedId?: string;
}

// 🔍 Расчеты продолжительности жизни
export interface LifeExpectancyCalculation {
  baseLifeExpectancy: number;
  habitAdjustments: Record<string, number>;
  lifestyleAdjustments: Record<string, number>;
  finalLifeExpectancy: number;
  confidenceInterval: [number, number];
  daysGainedFromQuits: number;
  medicalDisclaimer: string;
  lastCalculated: Date;
}

// 📱 Push уведомления
export interface PushNotificationData {
  title: string;
  body: string;
  type: NotificationType;
  data?: Record<string, any>;
}

export type NotificationType = 
  | 'CHALLENGE_REMINDER' 
  | 'HABIT_MILESTONE' 
  | 'ACHIEVEMENT_UNLOCKED' 
  | 'DAILY_MOTIVATION' 
  | 'STREAK_WARNING';

// 🌐 API ответы
export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
  errors?: Record<string, string[]>;
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// ⚠️ Ошибки
export interface ApiError {
  code: string;
  message: string;
  statusCode: number;
  details?: any;
}

// 🔧 Конфигурация
export interface AppConfig {
  port: number;
  nodeEnv: string;
  databaseUrl: string;
  jwtSecret: string;
  jwtExpiresIn: string;
  bcryptRounds: number;
  twilioAccountSid: string;
  twilioAuthToken: string;
  twilioPhoneNumber: string;
  corsOrigin: string;
  rateLimitWindowMs: number;
  rateLimitMax: number;
}

// 📊 Метрики для мониторинга
export interface SystemMetrics {
  uptime: number;
  memoryUsage: NodeJS.MemoryUsage;
  activeUsers: number;
  totalRegistrations: number;
  challengesInProgress: number;
  totalHabitsTracked: number;
}