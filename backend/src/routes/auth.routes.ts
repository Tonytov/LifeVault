// 🛣️ Маршруты аутентификации LifeVault

import { Router } from 'express';
import { authController } from '@/controllers/auth.controller';
import { 
  authenticateToken, 
  optionalAuth,
  csrfProtection 
} from '@/middleware/auth.middleware';
import { 
  validateSendCode, 
  validateVerifyCode, 
  validateRefreshToken 
} from '@/middleware/validation.middleware';

const router = Router();

/**
 * @swagger
 * tags:
 *   name: Authentication
 *   description: Аутентификация и управление сессиями пользователей
 */

/**
 * @swagger
 * /api/auth/send-code:
 *   post:
 *     summary: Отправка SMS кода верификации
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - phoneNumber
 *             properties:
 *               phoneNumber:
 *                 type: string
 *                 description: Номер телефона в международном формате
 *                 example: "+79123456789"
 *     responses:
 *       200:
 *         description: Код успешно отправлен
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Код верификации отправлен на ваш телефон"
 *       400:
 *         description: Ошибка валидации или отправки
 *       429:
 *         description: Слишком много запросов
 */
router.post('/send-code', 
  csrfProtection,
  validateSendCode, 
  authController.sendVerificationCode.bind(authController)
);

/**
 * @swagger
 * /api/auth/verify-code:
 *   post:
 *     summary: Верификация SMS кода и получение токенов
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - phoneNumber
 *               - code
 *             properties:
 *               phoneNumber:
 *                 type: string
 *                 description: Номер телефона
 *                 example: "+79123456789"
 *               code:
 *                 type: string
 *                 description: 6-значный код верификации
 *                 example: "123456"
 *     responses:
 *       200:
 *         description: Успешная аутентификация
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Аутентификация успешна"
 *                 data:
 *                   type: object
 *                   properties:
 *                     token:
 *                       type: string
 *                       description: JWT access токен
 *                     refreshToken:
 *                       type: string
 *                       description: Refresh токен
 *       400:
 *         description: Неверный код или ошибка валидации
 *       429:
 *         description: Слишком много попыток
 */
router.post('/verify-code', 
  csrfProtection,
  validateVerifyCode, 
  authController.verifyCode.bind(authController)
);

/**
 * @swagger
 * /api/auth/refresh:
 *   post:
 *     summary: Обновление access токена
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - refreshToken
 *             properties:
 *               refreshToken:
 *                 type: string
 *                 description: Refresh токен
 *     responses:
 *       200:
 *         description: Токен успешно обновлен
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Токен обновлен"
 *                 data:
 *                   type: object
 *                   properties:
 *                     token:
 *                       type: string
 *                       description: Новый JWT access токен
 *                     refreshToken:
 *                       type: string
 *                       description: Новый refresh токен
 *       401:
 *         description: Недействительный refresh токен
 */
router.post('/refresh', 
  validateRefreshToken, 
  authController.refreshToken.bind(authController)
);

/**
 * @swagger
 * /api/auth/logout:
 *   post:
 *     summary: Выход из системы
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - refreshToken
 *             properties:
 *               refreshToken:
 *                 type: string
 *                 description: Refresh токен для удаления
 *     responses:
 *       200:
 *         description: Успешный выход из системы
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Выход выполнен успешно"
 */
router.post('/logout', 
  validateRefreshToken, 
  authController.logout.bind(authController)
);

/**
 * @swagger
 * /api/auth/me:
 *   get:
 *     summary: Получение информации о текущем пользователе
 *     tags: [Authentication]
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: Информация о пользователе
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     id:
 *                       type: string
 *                       format: uuid
 *                     phoneNumber:
 *                       type: string
 *                       example: "+79123456789"
 *                     isPhoneVerified:
 *                       type: boolean
 *                       example: true
 *                     hasProfile:
 *                       type: boolean
 *                       example: true
 *                     profile:
 *                       type: object
 *                       nullable: true
 *                       properties:
 *                         firstName:
 *                           type: string
 *                           example: "Иван"
 *                         lastName:
 *                           type: string
 *                           example: "Петров"
 *                         gender:
 *                           type: string
 *                           enum: [MALE, FEMALE, OTHER]
 *                         region:
 *                           type: string
 *                           example: "RU"
 *                     createdAt:
 *                       type: string
 *                       format: date-time
 *                     lastLoginAt:
 *                       type: string
 *                       format: date-time
 *       401:
 *         description: Требуется аутентификация
 *       404:
 *         description: Пользователь не найден
 */
router.get('/me', 
  authenticateToken, 
  authController.getCurrentUser.bind(authController)
);

/**
 * @swagger
 * /api/auth/status:
 *   get:
 *     summary: Проверка статуса аутентификации
 *     tags: [Authentication]
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: Статус аутентификации
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     isAuthenticated:
 *                       type: boolean
 *                       example: true
 *                     user:
 *                       type: object
 *                       nullable: true
 *                       properties:
 *                         id:
 *                           type: string
 *                           format: uuid
 *                         phoneNumber:
 *                           type: string
 *                         isPhoneVerified:
 *                           type: boolean
 */
router.get('/status', 
  optionalAuth, 
  authController.checkAuthStatus.bind(authController)
);

/**
 * @swagger
 * /api/auth/delete-account:
 *   delete:
 *     summary: Удаление аккаунта пользователя
 *     tags: [Authentication]
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: Аккаунт успешно удален
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Аккаунт успешно удален"
 *       401:
 *         description: Требуется аутентификация
 *       500:
 *         description: Ошибка при удалении аккаунта
 */
router.delete('/delete-account', 
  authenticateToken, 
  authController.deleteAccount.bind(authController)
);

// 🔧 Административные маршруты
/**
 * @swagger
 * /api/auth/admin/cleanup-tokens:
 *   post:
 *     summary: Очистка истекших токенов (административная функция)
 *     tags: [Authentication]
 *     security:
 *       - AdminKeyAuth: []
 *     responses:
 *       200:
 *         description: Токены успешно очищены
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 message:
 *                   type: string
 *                   example: "Очищено 5 истекших токенов"
 *       403:
 *         description: Недостаточно прав доступа
 */
router.post('/admin/cleanup-tokens', 
  authController.cleanupTokens.bind(authController)
);

/**
 * @swagger
 * /api/auth/admin/stats:
 *   get:
 *     summary: Статистика аутентификации (административная функция)
 *     tags: [Authentication]
 *     security:
 *       - AdminKeyAuth: []
 *     responses:
 *       200:
 *         description: Статистика аутентификации
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     message:
 *                       type: string
 *                     timestamp:
 *                       type: string
 *                       format: date-time
 *       403:
 *         description: Недостаточно прав доступа
 */
router.get('/admin/stats', 
  authController.getAuthStats.bind(authController)
);

export default router;