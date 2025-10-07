// ✅ Middleware для валидации данных LifeVault

import Joi from 'joi';
import { Request, Response, NextFunction } from 'express';
import type { AuthenticatedRequest } from '@/types';

// 🔧 Базовые схемы валидации
const phoneSchema = Joi.string()
  .pattern(/^\+?[1-9]\d{1,14}$/)
  .required()
  .messages({
    'string.pattern.base': 'Неверный формат номера телефона',
    'any.required': 'Номер телефона обязателен'
  });

const codeSchema = Joi.string()
  .length(6)
  .pattern(/^\d{6}$/)
  .required()
  .messages({
    'string.length': 'Код должен содержать 6 цифр',
    'string.pattern.base': 'Код должен содержать только цифры',
    'any.required': 'Код верификации обязателен'
  });

// 📱 Валидация запроса отправки SMS кода
export const validateSendCode = (req: Request, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    phoneNumber: phoneSchema
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// ✅ Валидация запроса верификации кода
export const validateVerifyCode = (req: Request, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    phoneNumber: phoneSchema,
    code: codeSchema
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 👤 Валидация данных профиля пользователя
export const validateUserProfile = (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    firstName: Joi.string().min(1).max(50).optional(),
    lastName: Joi.string().min(1).max(50).optional(),
    dateOfBirth: Joi.date()
      .max('now')
      .min('1900-01-01')
      .required()
      .messages({
        'date.max': 'Дата рождения не может быть в будущем',
        'date.min': 'Некорректная дата рождения',
        'any.required': 'Дата рождения обязательна'
      }),
    gender: Joi.string()
      .valid('MALE', 'FEMALE', 'OTHER')
      .required()
      .messages({
        'any.only': 'Пол должен быть MALE, FEMALE или OTHER',
        'any.required': 'Пол обязателен'
      }),
    height: Joi.number()
      .integer()
      .min(100)
      .max(250)
      .required()
      .messages({
        'number.min': 'Рост должен быть не менее 100 см',
        'number.max': 'Рост должен быть не более 250 см',
        'any.required': 'Рост обязателен'
      }),
    weight: Joi.number()
      .integer()
      .min(20)
      .max(300)
      .required()
      .messages({
        'number.min': 'Вес должен быть не менее 20 кг',
        'number.max': 'Вес должен быть не более 300 кг',
        'any.required': 'Вес обязателен'
      }),
    region: Joi.string()
      .length(2)
      .uppercase()
      .required()
      .messages({
        'string.length': 'Регион должен быть кодом из 2 символов (например, RU)',
        'any.required': 'Регион обязателен'
      }),
    isPublicProfile: Joi.boolean().optional()
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации профиля',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 🚭 Валидация данных привычки
export const validateHabit = (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    type: Joi.string()
      .valid('SMOKING', 'ALCOHOL', 'SUGAR')
      .required()
      .messages({
        'any.only': 'Тип привычки должен быть SMOKING, ALCOHOL или SUGAR',
        'any.required': 'Тип привычки обязателен'
      }),
    isActive: Joi.boolean().required(),
    dailyUsage: Joi.number().integer().min(0).max(1000).optional(),
    costPerUnit: Joi.number().min(0).max(10000).optional(),
    unitsPerPack: Joi.number().integer().min(1).max(1000).optional(),
    startDate: Joi.date().max('now').optional(),
    quitDate: Joi.date().max('now').optional(),
    targetQuitDate: Joi.date().min('now').optional(),
    additionalInfo: Joi.string().max(500).optional()
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации привычки',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  // Дополнительная валидация логики дат
  const { startDate, quitDate, targetQuitDate } = req.body;
  
  if (startDate && quitDate && new Date(startDate) > new Date(quitDate)) {
    res.status(400).json({
      success: false,
      message: 'Дата начала не может быть позже даты отказа'
    });
    return;
  }
  
  if (startDate && targetQuitDate && new Date(startDate) > new Date(targetQuitDate)) {
    res.status(400).json({
      success: false,
      message: 'Дата начала не может быть позже целевой даты отказа'
    });
    return;
  }
  
  next();
};

// 🎯 Валидация начала вызова
export const validateStartChallenge = (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    challengeId: Joi.string().uuid().required().messages({
      'string.guid': 'challengeId должен быть валидным UUID',
      'any.required': 'ID вызова обязателен'
    })
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации вызова',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 🔄 Валидация refresh токена
export const validateRefreshToken = (req: Request, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    refreshToken: Joi.string().required().messages({
      'any.required': 'Refresh токен обязателен'
    })
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации токена',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 📊 Валидация параметров пагинации
export const validatePagination = (req: Request, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    page: Joi.number().integer().min(1).default(1),
    limit: Joi.number().integer().min(1).max(100).default(20),
    sortBy: Joi.string().optional(),
    sortOrder: Joi.string().valid('asc', 'desc').default('desc')
  });

  const { error, value } = schema.validate(req.query);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации параметров пагинации',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  // Заменяем query параметры на валидированные значения
  req.query = { ...req.query, ...value };
  
  next();
};

// 🏪 Валидация покупки в магазине времени
export const validateTimeBankPurchase = (req: AuthenticatedRequest, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    itemId: Joi.string().uuid().required().messages({
      'string.guid': 'itemId должен быть валидным UUID',
      'any.required': 'ID товара обязателен'
    }),
    quantity: Joi.number().integer().min(1).max(100).default(1)
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации покупки',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 📱 Валидация push уведомления
export const validatePushNotification = (req: Request, res: Response, next: NextFunction): void => {
  const schema = Joi.object({
    title: Joi.string().min(1).max(100).required(),
    body: Joi.string().min(1).max(500).required(),
    type: Joi.string()
      .valid('CHALLENGE_REMINDER', 'HABIT_MILESTONE', 'ACHIEVEMENT_UNLOCKED', 'DAILY_MOTIVATION', 'STREAK_WARNING')
      .required(),
    data: Joi.object().optional(),
    userIds: Joi.array().items(Joi.string().uuid()).optional()
  });

  const { error } = schema.validate(req.body);
  
  if (error) {
    res.status(400).json({
      success: false,
      message: 'Ошибка валидации уведомления',
      errors: formatJoiErrors(error)
    });
    return;
  }
  
  next();
};

// 🛠️ Утилита для форматирования ошибок Joi
function formatJoiErrors(error: Joi.ValidationError): Record<string, string[]> {
  const formattedErrors: Record<string, string[]> = {};
  
  error.details.forEach(detail => {
    const field = detail.path.join('.');
    if (!formattedErrors[field]) {
      formattedErrors[field] = [];
    }
    formattedErrors[field].push(detail.message);
  });
  
  return formattedErrors;
}

// 🔍 Middleware для валидации UUID параметров
export const validateUUIDParam = (paramName: string) => {
  return (req: Request, res: Response, next: NextFunction): void => {
    const schema = Joi.string().uuid().required();
    const { error } = schema.validate(req.params[paramName]);
    
    if (error) {
      res.status(400).json({
        success: false,
        message: `Недействительный ${paramName}`
      });
      return;
    }
    
    next();
  };
};