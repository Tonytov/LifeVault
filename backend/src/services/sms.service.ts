// 📱 Сервис SMS уведомлений через Twilio

import twilio from 'twilio';
import { config, isDevelopment } from '@/utils/config';
import { appLogger } from '@/utils/logger';

class SMSService {
  private client: twilio.Twilio;
  
  constructor() {
    this.client = twilio(config.twilioAccountSid, config.twilioAuthToken);
  }
  
  // 📨 Отправка кода верификации
  async sendVerificationCode(phoneNumber: string, code: string): Promise<{ success: boolean; message: string }> {
    try {
      // В режиме разработки имитируем отправку
      if (isDevelopment() && phoneNumber.includes('test')) {
        console.log(`📱 [DEV] SMS код для ${phoneNumber}: ${code}`);
        return {
          success: true,
          message: 'SMS код отправлен (режим разработки)'
        };
      }
      
      const messageBody = `Ваш код для входа в LifeVault: ${code}\n\nКод действителен 10 минут.\nНикому не сообщайте этот код!`;
      
      const message = await this.client.messages.create({
        body: messageBody,
        from: config.twilioPhoneNumber,
        to: phoneNumber,
      });
      
      if (message.sid) {
        appLogger.auth.login(phoneNumber, true);
        return {
          success: true,
          message: 'SMS код успешно отправлен'
        };
      } else {
        throw new Error('Не получен SID сообщения');
      }
      
    } catch (error) {
      appLogger.error.external('Twilio SMS', error as Error);
      
      // Возвращаем более понятное сообщение об ошибке
      let userMessage = 'Ошибка при отправке SMS';
      
      if (error instanceof Error) {
        if (error.message.includes('phone number')) {
          userMessage = 'Неверный формат номера телефона';
        } else if (error.message.includes('balance')) {
          userMessage = 'Временные проблемы со службой SMS';
        } else if (error.message.includes('rate limit')) {
          userMessage = 'Слишком много запросов, попробуйте позже';
        }
      }
      
      return {
        success: false,
        message: userMessage
      };
    }
  }
  
  // 🎯 Отправка напоминания о вызове
  async sendChallengeReminder(
    phoneNumber: string, 
    challengeTitle: string, 
    daysLeft: number
  ): Promise<{ success: boolean; message: string }> {
    try {
      const messageBody = `🎯 LifeVault: Напоминание о вызове "${challengeTitle}"\n\nОсталось дней: ${daysLeft}\nПродолжайте! Вы отлично справляетесь! 💪`;
      
      if (isDevelopment()) {
        console.log(`📱 [DEV] Challenge SMS для ${phoneNumber}: ${messageBody}`);
        return { success: true, message: 'Напоминание отправлено (DEV)' };
      }
      
      const message = await this.client.messages.create({
        body: messageBody,
        from: config.twilioPhoneNumber,
        to: phoneNumber,
      });
      
      return message.sid 
        ? { success: true, message: 'Напоминание отправлено' }
        : { success: false, message: 'Ошибка отправки напоминания' };
        
    } catch (error) {
      appLogger.error.external('Twilio Challenge Reminder', error as Error);
      return { success: false, message: 'Ошибка отправки напоминания' };
    }
  }
  
  // 🏆 Отправка поздравления с достижением
  async sendAchievementNotification(
    phoneNumber: string, 
    achievementName: string, 
    rewardDays: number
  ): Promise<{ success: boolean; message: string }> {
    try {
      const messageBody = `🏆 LifeVault: Поздравляем!\n\nВы получили достижение "${achievementName}"!\nНаграда: +${rewardDays} дней жизни\n\nТак держать! 🎉`;
      
      if (isDevelopment()) {
        console.log(`📱 [DEV] Achievement SMS для ${phoneNumber}: ${messageBody}`);
        return { success: true, message: 'Поздравление отправлено (DEV)' };
      }
      
      const message = await this.client.messages.create({
        body: messageBody,
        from: config.twilioPhoneNumber,
        to: phoneNumber,
      });
      
      return message.sid 
        ? { success: true, message: 'Поздравление отправлено' }
        : { success: false, message: 'Ошибка отправки поздравления' };
        
    } catch (error) {
      appLogger.error.external('Twilio Achievement', error as Error);
      return { success: false, message: 'Ошибка отправки поздравления' };
    }
  }
  
  // 📊 Отправка еженедельного отчета
  async sendWeeklyReport(
    phoneNumber: string, 
    stats: {
      daysWithoutHabits: number;
      moneySaved: number;
      challengesCompleted: number;
    }
  ): Promise<{ success: boolean; message: string }> {
    try {
      const messageBody = `📊 LifeVault: Ваш недельный отчет\n\nДней без вредных привычек: ${stats.daysWithoutHabits}\nСэкономлено: ${stats.moneySaved}₽\nВызовов выполнено: ${stats.challengesCompleted}\n\nВы молодец! 🌟`;
      
      if (isDevelopment()) {
        console.log(`📱 [DEV] Weekly Report SMS для ${phoneNumber}: ${messageBody}`);
        return { success: true, message: 'Отчет отправлен (DEV)' };
      }
      
      const message = await this.client.messages.create({
        body: messageBody,
        from: config.twilioPhoneNumber,
        to: phoneNumber,
      });
      
      return message.sid 
        ? { success: true, message: 'Отчет отправлен' }
        : { success: false, message: 'Ошибка отправки отчета' };
        
    } catch (error) {
      appLogger.error.external('Twilio Weekly Report', error as Error);
      return { success: false, message: 'Ошибка отправки отчета' };
    }
  }
  
  // ⚠️ Отправка предупреждения о срыве
  async sendStreakWarning(
    phoneNumber: string, 
    habitType: string, 
    currentStreak: number
  ): Promise<{ success: boolean; message: string }> {
    try {
      const habitNames = {
        SMOKING: 'курения',
        ALCOHOL: 'алкоголя', 
        SUGAR: 'сахара'
      };
      
      const habitName = habitNames[habitType as keyof typeof habitNames] || habitType;
      
      const messageBody = `⚠️ LifeVault: Мотивация\n\nВы идете ${currentStreak} дней без ${habitName}!\nНе сдавайтесь сейчас - вы так близко к цели!\n\nВаше здоровье бесценно! 💪`;
      
      if (isDevelopment()) {
        console.log(`📱 [DEV] Streak Warning SMS для ${phoneNumber}: ${messageBody}`);
        return { success: true, message: 'Мотивация отправлена (DEV)' };
      }
      
      const message = await this.client.messages.create({
        body: messageBody,
        from: config.twilioPhoneNumber,
        to: phoneNumber,
      });
      
      return message.sid 
        ? { success: true, message: 'Мотивация отправлена' }
        : { success: false, message: 'Ошибка отправки мотивации' };
        
    } catch (error) {
      appLogger.error.external('Twilio Streak Warning', error as Error);
      return { success: false, message: 'Ошибка отправки мотивации' };
    }
  }
  
  // 📞 Проверка статуса Twilio аккаунта
  async checkAccountStatus(): Promise<{ success: boolean; message: string; balance?: string }> {
    try {
      const account = await this.client.api.accounts(config.twilioAccountSid).fetch();
      
      return {
        success: true,
        message: `Аккаунт ${account.friendlyName} активен`,
        balance: account.status
      };
    } catch (error) {
      appLogger.error.external('Twilio Account Check', error as Error);
      return {
        success: false,
        message: 'Ошибка проверки статуса аккаунта Twilio'
      };
    }
  }
  
  // 📊 Получение статистики отправленных SMS
  async getSMSStatistics(days: number = 7): Promise<{ 
    success: boolean; 
    sent?: number; 
    failed?: number; 
    cost?: string 
  }> {
    try {
      const startDate = new Date();
      startDate.setDate(startDate.getDate() - days);
      
      const messages = await this.client.messages.list({
        dateSentAfter: startDate,
        limit: 1000
      });
      
      const sent = messages.filter(m => m.status === 'delivered').length;
      const failed = messages.filter(m => ['failed', 'undelivered'].includes(m.status)).length;
      
      return {
        success: true,
        sent,
        failed,
        cost: 'N/A' // Twilio не предоставляет стоимость через API
      };
    } catch (error) {
      appLogger.error.external('Twilio Statistics', error as Error);
      return { success: false };
    }
  }
}

export const smsService = new SMSService();