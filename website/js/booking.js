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
            <p class="service-description">${service.Описание}</p>
            <span class="service-price">${service.Цена} ₽</span>
            <span class="service-duration">${service.Длительность} мин.</span>
        </div>
    `).join('');
}
