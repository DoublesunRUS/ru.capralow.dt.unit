# language: en

@tree
@classname=ModuleExceptionPath

Feature: Тест_ОбщийМодульКлиентСервер
	As Developer
	I want the returns value to be equal to expected value
	That I can guarantee the execution of the method

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьИстинуНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьИстинуНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьИстинуНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьИстинуНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЛожьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЛожьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЛожьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЛожьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеДатыВПериодНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеДатыВПериодНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеДатыВПериодНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеДатыВПериодНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоДатСТочностью2СекундыНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоДатСТочностью2СекундыНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоДатСТочностью2СекундыНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоДатСТочностью2СекундыНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоСтрокиНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоСтрокиНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоЧислаНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоЧислаНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоЧислаНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоЧислаНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоБольшеНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоБольшеНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоБольшеИлиРавноНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеИлиРавноНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоБольшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеИлиРавноНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоМеньшеНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоМеньшеНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоМеньшеИлиРавноНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеИлиРавноНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЧислоМеньшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеИлиРавноНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВыполнилосьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВыполнилосьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВыполнилосьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВыполнилосьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьМетодВыполнилсяНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодВыполнилсяНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьМетодВыполнилсяНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодВыполнилсяНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеВыполнилосьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеВыполнилосьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеВыполнилосьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеВыполнилосьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьМетодНеВыполнилсяНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодНеВыполнилсяНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьМетодНеВыполнилсяНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодНеВыполнилсяНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЗаполненностьНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗаполненностьНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЗаполненностьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗаполненностьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеЗаполненностьНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеЗаполненностьНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьНеЗаполненностьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеЗаполненностьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЗначениеНаТипНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗначениеНаТипНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьЗначениеНаТипНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗначениеНаТипНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеСтрокиНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеСтрокиНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеЭлементаКоллекцииНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеЭлементаКоллекцииНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьВхождениеЭлементаКоллекцииНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеЭлементаКоллекцииНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоКоллекцийНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоКоллекцийНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодульКлиентСервер (server): ПроверитьРавенствоКоллекцийНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоКоллекцийНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьИстинуНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьИстинуНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьИстинуНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьИстинуНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЛожьНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЛожьНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЛожьНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЛожьНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеДатыВПериодНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеДатыВПериодНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеДатыВПериодНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеДатыВПериодНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоДатСТочностью2СекундыНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоДатСТочностью2СекундыНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоДатСТочностью2СекундыНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоДатСТочностью2СекундыНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоСтрокиНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоСтрокиНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоСтрокиНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоСтрокиНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоЧислаНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоЧислаНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоЧислаНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеРавенствоЧислаНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоБольшеНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоБольшеНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоБольшеИлиРавноНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеИлиРавноНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоБольшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоБольшеИлиРавноНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоМеньшеНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоМеньшеНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоМеньшеИлиРавноНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеИлиРавноНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЧислоМеньшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЧислоМеньшеИлиРавноНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВыполнилосьНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВыполнилосьНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВыполнилосьНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВыполнилосьНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьМетодВыполнилсяНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодВыполнилсяНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьМетодВыполнилсяНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодВыполнилсяНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеВыполнилосьНаИстину
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеВыполнилосьНаИстину(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеВыполнилосьНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеВыполнилосьНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьМетодНеВыполнилсяНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодНеВыполнилсяНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьМетодНеВыполнилсяНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьМетодНеВыполнилсяНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЗаполненностьНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗаполненностьНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЗаполненностьНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗаполненностьНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеЗаполненностьНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеЗаполненностьНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьНеЗаполненностьНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьНеЗаполненностьНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЗначениеНаТипНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗначениеНаТипНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьЗначениеНаТипНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьЗначениеНаТипНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеСтрокиНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеСтрокиНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеСтрокиНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеСтрокиНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеЭлементаКоллекцииНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеЭлементаКоллекцииНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьВхождениеЭлементаКоллекцииНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьВхождениеЭлементаКоллекцииНаЛожь(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоКоллекцийНаИстина
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоКоллекцийНаИстина(Context());' |

Scenario: Тест_ОбщийМодульКлиентСервер (client): ПроверитьРавенствоКоллекцийНаЛожь
	And I execute 1C:Enterprise script
	| 'Тест_ОбщийМодульКлиентСервер.ПроверитьРавенствоКоллекцийНаЛожь(Context());' |