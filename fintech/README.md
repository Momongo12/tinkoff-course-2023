# Системы автоматической сборки

## Оглавление

1. [Предварительные требования](#предварительные-требования)
2. [Сборка и запуск fatJar](#сборка-и-запуск-fatjar)
3. [Скриншоты](#скриншоты)

## Предварительные требования

Для успешной сборки и запуска проекта убедитесь, что у вас установлены:

- Git
- JDK (версия 8 или выше)
- Gradle 7+ (если не установлен, проект будет использовать gradle wrapper)

## Сборка и запуск fatJar

Следуйте пошаговой инструкции ниже:

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/Momongo12/tinkoff-course-2023.git
   ```

2. **Переход в директорию проекта**
   ```bash
   cd tinkoff-course-2023
   ```

3. **Переключение на ветку `build-systems`**  
   Пока код проекта `fintech` находится в этой ветке.
   ```bash
   git checkout -t origin/build-systems
   ```

4. **Переход в папку `fintech`**
   ```bash
   cd fintech
   ```

5. **Создание fatJar**
   ```bash
   ./gradlew createFatJar
   ```

6. **Запуск fatJar**
   ```bash
   java -jar ./build/libs/fintech-1.0-SNAPSHOT.jar
   ```

## Скриншоты
Вот так это выглядит у меня
![](https://i.ibb.co/LQWLJ3j/image.png)