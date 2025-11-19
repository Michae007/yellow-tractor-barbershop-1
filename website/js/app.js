// Основной JavaScript код приложения
document.addEventListener('DOMContentLoaded', function() {
    console.log('Детская парикмахерская "Жёлтый трактор" загружена!');
    
    // Инициализация приложения
    initApp();
});

async function initApp() {
    // Плавная прокрутка к якорям
    initSmoothScroll();
    
    // Загрузка услуг
    await loadServices();
    
    // Инициализация системы записи
    initBookingSystem();
    
    // Обработка модального окна
    initModal();
}

// Плавная прокрутка
function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                const headerHeight = document.querySelector('.header').offsetHeight;
                const targetPosition = targetElement.offsetTop - headerHeight - 20;
                
                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Загрузка услуг
async function loadServices() {
    try {
        const services = await GASAPI.getServices();
        renderServices(services);
        populateServiceSelect(services);
    } catch (error) {
        console.error('Ошибка загрузки услуг:', error);
        showError('Не удалось загрузить услуги. Пожалуйста, обновите страницу.');
    }
}

// Отображение услуг на странице
function renderServices(services) {
    const servicesGrid = document.getElementById('services-grid');
    
    if (!services || services.length === 0) {
        servicesGrid.innerHTML = '<p class="error-message">Услуги временно недоступны</p>';
        return;
    }
    
    servicesGrid.innerHTML = services.map(service => `
        <div class="service-card">
            <div class="service-icon">${getServiceIcon(service.Услуга)}</div>
            <h3>${service.Услуга}</h3>
            <p>${service.Описание}</p>
            <span class="price">${service.Цена} руб.</span>
            <span class="duration">${service.Длительность} мин.</span>
        </div>
    `).join('');
}

// Заполнение select услуг
function populateServiceSelect(services) {
    const serviceSelect = document.getElementById('service');
    
    if (!services || services.length === 0) return;
    
    // Очищаем существующие опции, кроме первой
    while (serviceSelect.children.length > 1) {
        serviceSelect.removeChild(serviceSelect.lastChild);
    }
    
    services.forEach(service => {
        const option = document.createElement('option');
        option.value = service.Услуга;
        option.textContent = `${service.Услуга} - ${service.Цена} руб. (${service.Длительность} мин.)`;
        serviceSelect.appendChild(option);
    });
}

// Иконки для услуг
function getServiceIcon(serviceName) {
    const icons = {
        'Детская стрижка': '✂️',
        'Стрижка с укладкой': '👧',
        'Первая стрижка': '👶',
        'Креативное окрашивание': '🎨'
    };
    
    return icons[serviceName] || '💇';
}

// Инициализация модального окна
function initModal() {
    const modal = document.getElementById('success-modal');
    const closeBtn = document.querySelector('.close');
    const modalCloseBtn = document.getElementById('modal-close');
    
    closeBtn.addEventListener('click', () => hideModal());
    modalCloseBtn.addEventListener('click', () => hideModal());
    
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            hideModal();
        }
    });
}

// Показать модальное окно
function showModal() {
    const modal = document.getElementById('success-modal');
    modal.style.display = 'block';
    document.body.style.overflow = 'hidden';
}

// Скрыть модальное окно
function hideModal() {
    const modal = document.getElementById('success-modal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
}

// Показать ошибку
function showError(message) {
    // Можно реализовать красивый toast или уведомление
    alert(message);
}

// Показать успех
function showSuccess(message) {
    // Можно реализовать красивый toast
    console.log('Success:', message);
}
