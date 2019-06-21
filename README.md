# dt.unit.launcher [![Build Status](https://travis-ci.org/DoublesunRUS/ru.capralow.dt.unit.launcher.svg)](https://travis-ci.org/DoublesunRUS/ru.capralow.dt.unit.launcher) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=alert_status)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.unit.launcher&metric=coverage)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.unit.launcher)


## Запуск модульных тестов для [1C:Enterprise Development Tools](http://v8.1c.ru/overview/IDE/) 1.10

Минимальная версия EDT: 1.11.0

Текущий релиз в ветке [master: 0.2.0](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/master).<br>
Разработка ведется в ветке [dev](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/dev).<br>

В данном репозитории хранятся только исходники.<br>

Плагин можно установить в EDT через пункт "Установить новое ПО" указав сайт обновления http://capralow.ru/edt/unit.launcher/latest/ .<br>
Для самостоятельной сборки плагина необходимо иметь доступ к сайту https://releases.1c.ru и настроить соответствующим образом Maven. Подробности настройки написаны [здесь](https://github.com/1C-Company/dt-example-plugins/blob/master/simple-plugin/README.md).<br>

### Возможности
Плагин позволяет запустить модульные тесты (unit tests) одной кнопкой, после чего увидеть результаты выполнения всех тестов в панели JUnit.<br>
Запуск модульных тестов осуществляется через специализированную конфигурацию запуска "Модульные тесты 1С:Предприятия".<br>
Плагин создает feature файлы в формате Gherkin, которые можно использовать для запуска модульных тестов в CI.

### Установка
Так как плагин использует JDT, для корректной установки его в EDT необходимо в список "Доступные сайты обновлений" добавить сайт:<br>
http://download.eclipse.org/releases/2019-03/<br>
Видео с демонстрацией установки можно посмотреть [здесь](https://youtu.be/BCQfR_Ve444).

### Принципы работы
Разработчик создает общий модуль, в котором пишет экспортные процедуры для тестирования своего функционала.<br>
Перед каждой процедурой, которая является тестом, необходимо добавить строку "// @unit-test". Тогда при сохранении общего модуля в подпапке features проекта с общим модулем будет создана feature для запуска теста. Если к специальной строке добавить ":" и некий текст, то будет создана подпапка и feature будет размещена в ней. Например если написать "// @unit-test:slow", то feature файл будет размещен в "features/slow". Это позволит пропускать медленные тесты при тестировании во время разработки.<br>
Тестовая процедура должна сравнивать эталонный результат с фактическим и вызывать исключение, если результаты не совпадают.<br>
Запуск тестовых процедур производится фреймворком тестирования - специальной внешней обработкой. Например [vanessa-automation](https://github.com/Pr-Mex/vanessa-automation). Требования к фреймворку тестирования написаны [здесь](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/blob/master/TEST_FRAMEWORK_API.md) <br>
Фреймворк тестирования возвращает файл junit.xml как разультат своей работы и содержимое этого файла отображается в панели JUnit.

### Настройка
В новое или существующее расширение добавить общий модуль, в котором создать процедуру для тестирования.<br>
Нажать ПКМ на проекте расширения, выбрать "Отладить как - Модульные тесты 1С:Предприятия".<br>
Видео-инструкцию по настройке можно посмотреть [здесь](https://youtu.be/No_BHUo1nSQ).<br>
<br>

### Демонстрация возможностей модульных тестов с использованием vanessa-automation-single
В папке [МодульныеТестыVA](https://github.com/DoublesunRUS/ru.capralow.dt.unit.launcher/tree/master/МодульныеТестыVA) находится расширение, демонстрирующее основные возможности программного интерфейса фреймворка тестирования<br>
Расширение можно установить вместе с любой конфигурацией и запустить тестирование.<br>
