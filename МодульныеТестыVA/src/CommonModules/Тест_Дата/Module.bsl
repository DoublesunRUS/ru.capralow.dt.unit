// @unit-test
Процедура ПроверитьВхождениеДатыВПериодНаИстину(Фреймворк) Экспорт
	ЭталонныйПериод = Новый СтандартныйПериод(Дата(2019, 1, 1), Дата(2019, 12, 31));
	ПроверяемаяДата = Дата(2019, 7, 1);
	
	Фреймворк.ПроверитьДату(ЭталонныйПериод, ПроверяемаяДата,
		"Дата входит в период.");
КонецПроцедуры

// @unit-test
Процедура ПроверитьВхождениеДатыВПериодНаЛожь(Фреймворк) Экспорт
	ЭталонныйПериод = Новый СтандартныйПериод(Дата(2019, 1, 1), Дата(2019, 12, 31));
	ПроверяемаяДата = Дата(2018, 7, 1);
	
	Фреймворк.ПроверитьДату(ЭталонныйПериод, ПроверяемаяДата,
		"Дата ошибочно не входит в период.");
КонецПроцедуры

// @unit-test
Процедура ПроверитьРавенствоДатСТочностью2СекундыНаИстину(Фреймворк) Экспорт
	ЭталоннаяДата	= Дата(2019, 7, 1, 0, 0, 1);
	ПроверяемаяДата	= Дата(2019, 7, 1, 0, 0, 3);
	
	Фреймворк.ПроверитьРавенствоДатСТочностью2Секунды(ЭталоннаяДата, ПроверяемаяДата,
		"Даты не различаются с точностью 2 секунды.");
КонецПроцедуры

// @unit-test
Процедура ПроверитьРавенствоДатСТочностью2СекундыНаЛожь(Фреймворк) Экспорт
	ЭталоннаяДата	= Дата(2019, 7, 1, 0, 0, 1);
	ПроверяемаяДата	= Дата(2019, 7, 1, 0, 0, 4);
	
	// FIXME: В Actual нужно выводить диапазон
	Фреймворк.ПроверитьРавенствоДатСТочностью2Секунды(ЭталоннаяДата, ПроверяемаяДата,
		"Даты ошибочно различаются с точностью 2 секунды.");
КонецПроцедуры

