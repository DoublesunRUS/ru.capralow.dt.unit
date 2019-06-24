# language: en

@tree
@classname=ModuleExceptionPath

Feature: Тест_ОбщийМодуль
	As Developer
	I want the returns value to be equal to expected value
	That I can guarantee the execution of the method

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьИстинуНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьИстинуНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьИстинуНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьИстинуНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЛожьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЛожьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЛожьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЛожьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеДатыВПериодНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеДатыВПериодНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеДатыВПериодНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеДатыВПериодНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоДатСТочностью2СекундыНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоДатСТочностью2СекундыНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоДатСТочностью2СекундыНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоДатСТочностью2СекундыНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоСтрокиНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеРавенствоСтрокиНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеРавенствоСтрокиНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеРавенствоСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеРавенствоСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоЧислаНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоЧислаНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеРавенствоЧислаНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеРавенствоЧислаНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеРавенствоЧислаНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеРавенствоЧислаНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоБольшеНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоБольшеНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоБольшеНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоБольшеНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоБольшеИлиРавноНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоБольшеИлиРавноНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоБольшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоБольшеИлиРавноНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоМеньшеНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоМеньшеНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоМеньшеНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоМеньшеНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоМеньшеИлиРавноНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоМеньшеИлиРавноНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЧислоМеньшеИлиРавноНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЧислоМеньшеИлиРавноНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВыполнилосьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВыполнилосьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВыполнилосьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВыполнилосьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьМетодВыполнилсяНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьМетодВыполнилсяНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьМетодВыполнилсяНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьМетодВыполнилсяНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеВыполнилосьНаИстину
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеВыполнилосьНаИстину(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеВыполнилосьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеВыполнилосьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьМетодНеВыполнилсяНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьМетодНеВыполнилсяНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьМетодНеВыполнилсяНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьМетодНеВыполнилсяНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЗаполненностьНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЗаполненностьНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЗаполненностьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЗаполненностьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеЗаполненностьНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеЗаполненностьНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьНеЗаполненностьНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьНеЗаполненностьНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЗначениеНаТипНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЗначениеНаТипНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьЗначениеНаТипНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьЗначениеНаТипНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеСтрокиНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеСтрокиНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеСтрокиНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеСтрокиНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеЭлементаКоллекцииНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеЭлементаКоллекцииНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьВхождениеЭлементаКоллекцииНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьВхождениеЭлементаКоллекцииНаЛожь(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоКоллекцийНаИстина
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоКоллекцийНаИстина(Context());' |

@OnServer
Scenario: Тест_ОбщийМодуль (server): ПроверитьРавенствоКоллекцийНаЛожь
	And I execute 1C:Enterprise script at server
	| 'Тест_ОбщийМодуль.ПроверитьРавенствоКоллекцийНаЛожь(Context());' |