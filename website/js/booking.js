// Система онлайн записи
let currentDate = new Date();
let selectedDate = null;
let selectedTime = null;

function initBookingSystem() {
    // Инициализация календаря
    initCalendar();
    
    // Обработка формы записи
    const bookingForm = document.getElementById('booking-form');
    bookingForm.addEventListener('submit', handleBookingSubmit);
    
    // Загрузка начальных данных
    updateTimeSlots();
}

// Инициализация календаря
function initCalendar() {
    renderCalendar(currentDate);
    
    // Обработчики навигации
    document.getElementById('prev-month').addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar(currentDate);
    });
    
    document.getElementById('next-month').addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar(currentDate);
    });
}

// Отрисовка календаря
function renderCalendar(date) {
    const calendar = document.getElementById('calendar');
    const monthYear = document.getElementById('current-month');
    
    // Установка заголовка
    const monthNames = [
        'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
    ];
    
    monthYear.textContent = `${monthNames[date.getMonth()]} ${date.getFullYear()}`;
    
    // Очистка календаря
    calendar.innerHTML = '';
    
    // Заголовки дней недели
    const dayNames = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
    dayNames.forEach(day => {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day-header';
        dayElement.textContent = day;
        calendar.appendChild(dayElement);
    });
    
    // Получение данных месяца
    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    const daysInMonth = lastDay.getDate();
    
    // Настройка первого дня
    let firstDayOfWeek = firstDay.getDay();
    if (firstDayOfWeek === 0) firstDayOfWeek = 7;
    
    // Пустые ячейки перед первым днем
    for (let i = 1; i < firstDayOfWeek; i++) {
        const emptyDay = document.createElement('div');
        emptyDay.className = 'calendar-day other-month';
        calendar.appendChild(emptyDay);
    }
    
    // Дни месяца
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    for (let i = 1; i <= daysInMonth; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day';
        dayElement.textContent = i;
        
        const currentDay = new Date(date.getFullYear(), date.getMonth(), i);
        currentDay.setHours(0, 0, 0, 0);
        
        // Проверка на воскресенье (выходной)
        const dayOfWeek = currentDay.getDay();
        if (dayOfWeek === 0) {
            dayElement.classList.add('weekend');
        }
        
        // Проверка на прошедшие дни
        if (currentDay < today) {
            dayElement.classList.add('past');
        } else if (dayOfWeek !== 0) { // Не воскресенье и не прошедший день
            dayElement.addEventListener('click', () => selectDate(currentDay, dayElement));
        }
        
        // Подсветка сегодняшнего дня
        if (currentDay.toDateString() === today.toDateString()) {
            dayElement.style.backgroundColor = 'rgba(255, 193, 7, 0.2)';
        }
        
        calendar.appendChild(dayElement);
    }
}

// Выбор даты
async function selectDate(date, element) {
    // Сброс предыдущего выбора
    const previouslySelected = document.querySelector('.calendar-day.active');
    if (previouslySelected) {
        previouslySelected.classList.remove('active');
    }
    
    // Установка нового выбора
    element.classList.add('active');
    selectedDate = date;
    
    // Сброс выбранного времени
    selectedTime = null;
    const previouslySelectedTime = document.querySelector('.time-slot.active');
    if (previouslySelectedTime) {
        previouslySelectedTime.classList.remove('active');
    }
    
    // Обновление временных слотов
    await updateTimeSlots();
}

// Обновление временных слотов
async function updateTimeSlots() {
    const timeSlotsContainer = document.getElementById('time-slots');
    
    if (!selectedDate) {
        timeSlotsContainer.innerHTML = '<p>Выберите дату для просмотра доступного времени</p>';
        return;
    }
    
    // Показываем загрузку
    timeSlotsContainer.innerHTML = '<div class="loading"></div>';
    
    try {
        // Получаем занятые слоты
        const dateString = formatDate(selectedDate);
        const bookedSlots = await GASAPI.getBookedSlots(dateString);
        
        // Генерируем слоты
        renderTimeSlots(bookedSlots);
    } catch (error) {
        console.error('Ошибка загрузки слотов:', error);
        timeSlotsContainer.innerHTML = '<p class="error-message">Ошибка загрузки времени. Попробуйте еще раз.</p>';
    }
}

// Отрисовка временных слотов
function renderTimeSlots(bookedSlots = []) {
    const timeSlotsContainer = document.getElementById('time-slots');
    timeSlotsContainer.innerHTML = '';
    
    // Проверка на воскресенье
    if (selectedDate.getDay() === 0) {
        timeSlotsContainer.innerHTML = '<p class="weekend-message">Воскресенье - выходной день</p>';
        return;
    }
    
    // Генерация слотов с 10:00 до 19:00 каждые 30 минут
    const startHour = 10;
    const endHour = 19;
    let hasAvailableSlots = false;
    
    for (let hour = startHour; hour < endHour; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const timeSlot = document.createElement('div');
            const timeString = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
            
            timeSlot.className = 'time-slot';
            timeSlot.textContent = timeString;
            
            // Проверка доступности
            const now = new Date();
            const selectedDateTime = new Date(selectedDate);
            selectedDateTime.setHours(hour, minute, 0, 0);
            
            const isPast = selectedDate.toDateString() === now.toDateString() && selectedDateTime < now;
            const isBooked = bookedSlots.includes(timeString);
            
            if (isPast || isBooked) {
                timeSlot.classList.add('booked');
            } else {
                hasAvailableSlots = true;
                timeSlot.addEventListener('click', () => selectTime(timeString, timeSlot));
            }
            
            timeSlotsContainer.appendChild(timeSlot);
        }
    }
    
    if (!hasAvailableSlots) {
        timeSlotsContainer.innerHTML = '<p class="no-slots-message">На выбранную дату нет свободных слотов</p>';
    }
}

// Выбор времени
function selectTime(time, element) {
    // Сброс предыдущего выбора
    const previouslySelected = document.querySelector('.time-slot.active');
    if (previouslySelected) {
        previouslySelected.classList.remove('active');
    }
    
    // Установка нового выбора
    element.classList.add('active');
    selectedTime = time;
}

// Форматирование даты
function formatDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Обработка отправки формы
async function handleBookingSubmit(e) {
    e.preventDefault();
    
    // Валидация
    if (!selectedDate || !selectedTime) {
        alert('Пожалуйста, выберите дату и время записи');
        return;
    }
    
    // Получение данных формы
    const formData = {
        clientName: document.getElementById('client-name').value.trim(),
        clientAge: document.getElementById('client-age').value,
        parentName: document.getElementById('parent-name').value.trim(),
        phone: document.getElementById('phone').value.trim(),
        service: document.getElementById('service').value,
        notes: document.getElementById('notes').value.trim(),
        date: formatDate(selectedDate),
        time: selectedTime
    };
    
    // Дополнительная валидация
    if (!formData.clientName || !formData.parentName || !formData.phone || !formData.service) {
        alert('Пожалуйста, заполните все обязательные поля');
        return;
    }
    
    // Показ загрузки
    const submitButton = e.target.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.innerHTML = '<div class="loading"></div> Отправка...';
    submitButton.disabled = true;
    
    try {
        // Отправка данных
        const result = await GASAPI.addBooking(formData);
        
        if (result.success) {
            // Успех
            showModal();
            resetForm();
        } else {
            // Ошибка
            alert('Ошибка при создании записи: ' + result.message);
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при отправке данных. Пожалуйста, попробуйте еще раз или позвоните нам.');
    } finally {
        // Восстановление кнопки
        submitButton.textContent = originalText;
        submitButton.disabled = false;
    }
}

// Сброс формы
function resetForm() {
    document.getElementById('booking-form').reset();
    
    selectedDate = null;
    selectedTime = null;
    
    // Сброс выбранных элементов
    const selectedDateElement = document.querySelector('.calendar-day.active');
    if (selectedDateElement) {
        selectedDateElement.classList.remove('active');
    }
    
    const selectedTimeElement = document.querySelector('.time-slot.active');
    if (selectedTimeElement) {
        selectedTimeElement.classList.remove('active');
    }
    
    // Обновление слотов
    updateTimeSlots();
}
