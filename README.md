# dt.unit.launcher [![Build Status](https://travis-ci.org/DoublesunRUS/ru.capralow.dt.unit.launcher.svg)](https://travis-ci.org/DoublesunRUS/ru.capralow.dt.unit.launcher) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=alert_status)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=coverage)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher)


## Запуск модульных тестов для [1C:Enterprise Development Tools](http://v8.1c.ru/overview/IDE/) 1.11

Минимальная версия EDT: 1.11.0

Текущий релиз в ветке [master: 0.2.0](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/master).<br>
Разработка ведется в ветке [dev](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/dev).<br>

В данном репозитории хранятся только исходники.<br>

Плагин можно установить в EDT через пункт "Установить новое ПО" указав сайт обновления http://capralow.ru/edt/unit.launcher/latest/ . Для установки может потребоваться запуск EDT под правами администратора.<br>
Для самостоятельной сборки плагина необходимо иметь доступ к сайту https://releases.1c.ru и настроить соответствующим образом Maven. Подробности настройки написаны [здесь](https://github.com/1C-Company/dt-example-plugins/blob/master/simple-plugin/README.md).

### Возможности
Плагин позволяет запустить модульные тесты (unit tests) одной кнопкой, после чего увидеть результаты выполнения всех тестов в панели JUnit.<br>
Запуск модульных тестов осуществляется через специализированную конфигурацию запуска "Модульные тесты 1С:Предприятия".<br>
Конфигурация запуска выполняет запуск 1С:Предприятия вместе с внешней обработкой - Фреймворком тестирования. Запуск тестовых процедур и сравнение результатов осуществляется этой обработкой.<br>
Плагин создает feature файлы в формате Gherkin, которые можно использовать для запуска модульных тестов в CI.<br>
Для отображения результатов тестов используется стандартная панель JUnit.

### Правила написания тестовых процедур
Перед каждой процедурой, которая является тестом, необходимо добавить строку "// @unit-test". Тогда при сохранении общего модуля в папке "features/all" проекта с общим модулем будет создана feature для запуска теста.<br>
Если к специальной строке добавить ":" и некий текст, то будет создана еще одна папка и feature файл будет размещен в двух папках. Например если написать "// @unit-test:slow", то feature файл будет размещен как в папке "features/all", так и в папке "features/slow".<br>
У каждой тестовой процедуры должен быть один параметр - "Фреймворк". В этот параметр при запуске тестов будет передан контекст формы Фреймворка тестирования.<br>
Тестовая процедура должна сравнивать эталонный результат с фактическим и вызывать исключение, если результаты не совпадают. Сравнение должно осуществляться через вызов специализированных функций фреймворка тестирования.

### Фреймворки тестирования
Фреймворки тестирования поставляются вместе с плагином и не требуют отдельной установки.<br>
Поддерживаемые фреймворки:<br>
[Vanessa Automation](https://github.com/Pr-Mex/vanessa-automation) 1.2.025.

### Использование Vanessa automation в качестве фреймворка тестирования
В папке [МодульныеТестыVA](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/master/МодульныеТестыVA) находится расширение, демонстрирующее основные возможности программного интерфейса фреймворка тестирования<br>
Расширение можно установить вместе с любой конфигурацией и запустить тестирование.

### Настройки запуска тестов
По умолчанию конфигурация запуска выполняет все тесты выбранного расширения. Для запуска тестов достаточно нажать ПКМ на проекте расширения и выбрать "Отладить как - Модульные тесты 1С:Предприятия".<br>
Для запуска тестов только одного общего модуля или всех тестов определенного теста необходимо донастроить конфигурацию запуска.

### Видео-инструкции
* [Установка](http://youtube.com).<br>
* [Начальная настройка](http://youtube.com).<br>
* [Расширенные настройки](http://youtube.com).<br>


