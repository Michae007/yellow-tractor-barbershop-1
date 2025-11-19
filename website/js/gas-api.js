// Конфигурация API
// ЗАМЕНИТЕ ЭТОТ URL НА ВАШ URL ИЗ GOOGLE APPS SCRIPT
const GAS_WEB_APP_URL = 'https://script.google.com/macros/s/ВАШ_ID/exec';

// Базовые функции API
class GASAPI {
    static async request(endpoint, data = null, method = 'GET') {
        const url = new URL(GAS_WEB_APP_URL);
        
        if (method === 'GET' && data) {
            Object.keys(data).forEach(key => url.searchParams.append(key, data[key]));
        }
        
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
        };
        
        if (method === 'POST' && data) {
            options.body = JSON.stringify(data);
        }
        
        try {
            const response = await fetch(url.toString(), options);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('API Request failed:', error);
            throw error;
        }
    }

    // Получение услуг
    static async getServices() {
        return await this.request('', { action: 'getServices' }, 'GET');
    }

    // Получение всех записей
    static async getBookings() {
        return await this.request('', { action: 'getBookings' }, 'GET');
    }

    // Получение записей по дате
    static async getBookingsByDate(date) {
        return await this.request('', { action: 'getBookingsByDate', date: date }, 'GET');
    }

    // Получение занятых слотов
    static async getBookedSlots(date) {
        return await this.request('', { action: 'getBookedSlots', date: date }, 'GET');
    }

    // Создание новой записи
    static async addBooking(bookingData) {
        return await this.request('', { action: 'addBooking', ...bookingData }, 'POST');
    }
}
