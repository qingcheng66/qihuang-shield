-- ====================================================================
-- 岐黄盾 (HerbShield) - 权威中药饮片与国家名方初始种子数据
-- 依据：《中华人民共和国药典》(2020年版) 与 国家中医药管理局古代经典名方目录
-- ====================================================================

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('麻黄', 'Mahuang', '["草麻黄", "中麻黄", "炙麻黄", "麻黄绒"]'::jsonb, '温', '辛、微苦', '归肺、膀胱经', '无毒', 9.0, '2~9g', '发汗解表，宣肺平喘，利水消肿', '体虚自汗、盗汗及虚喘者禁服。高血压、失眠患者慎服。', '常规水煎，解表宜生用，止咳平喘多炙用')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('桂枝', 'Guizhi', '["柳桂", "嫩桂枝", "桂枝尖"]'::jsonb, '温', '辛、甘', '归心、肺、膀胱经', '无毒', 10.0, '3~10g', '发汗解肌，温通经脉，助阳化气，平冲降气', '温热病、阴虚阳亢、出血证及孕妇慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('紫苏叶', 'Zisuye', '["苏叶", "紫苏"]'::jsonb, '温', '辛', '归肺、脾经', '无毒', 10.0, '5~10g', '解表散寒，行气和胃，解鱼蟹毒', '温病及气虚表弱者慎服。', '不宜久煎，后下')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('防风', 'Fangfeng', '["关防风", "东防风"]'::jsonb, '微温', '辛、甘', '归膀胱、肝、脾经', '无毒', 10.0, '5~10g', '祛风解表，胜湿止痛，止痉', '血虚发痉、阴虚火旺者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('羌活', 'Qianghuo', '["川羌活", "蚕羌", "竹节羌"]'::jsonb, '温', '辛、苦', '归膀胱、肾经', '无毒', 10.0, '3~10g', '散寒祛风，胜湿止痛', '阴虚头痛、血虚痹痛者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('白芷', 'Baizhi', '["川白芷", "杭白芷", "香白芷"]'::jsonb, '温', '辛', '归胃、大肠、肺经', '无毒', 10.0, '3~10g', '解表散寒，祛风止痛，宣通鼻窍，燥湿止带，消肿排脓', '阴虚血热者忌服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('细辛', 'Xixin', '["辽细辛", "北细辛", "细草"]'::jsonb, '温', '辛', '归心、肺、肾经', '有小毒', 3.0, '1~3g', '祛风散寒，通窍止痛，温肺化饮', '【十八反】：反藜芦！气虚多汗、阴虚阳亢头痛及肺热咳嗽者禁服。“细辛不过钱”，单剂超3g有呼吸抑制风险。', '入汤剂不可超 3g，严格称量')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('生姜', 'Shengjiang', '["鲜生姜", "姜皮"]'::jsonb, '微温', '辛', '归肺、脾、胃经', '无毒', 10.0, '3~10g', '解表散寒，温中止呕，化痰止咳，解鱼蟹半夏毒', '阴虚内热及热盛者忌用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('柴胡', 'Chaihu', '["北柴胡", "南柴胡", "醋柴胡"]'::jsonb, '微寒', '辛、苦', '归肝、胆、肺经', '无毒', 10.0, '3~10g', '疏散退热，疏肝解郁，升举阳气', '真阴亏损、肝阳上亢者忌服。', '解热生用且量宜大，升阳疏肝宜醋炙')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('葛根', 'Gegen', '["粉葛", "野葛", "煨葛根"]'::jsonb, '凉', '甘、辛', '归脾、胃、肺经', '无毒', 30.0, '10~15g', '解肌退热，生津止渴，透疹，升阳止泻，通经活络，解酒毒', '脾胃虚寒者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('薄荷', 'Bohe', '["苏薄荷", "鲜薄荷"]'::jsonb, '凉', '辛', '归肺、肝经', '无毒', 6.0, '3~6g', '疏散风热，清利头目，利咽透疹，疏肝行气', '阴虚血燥、体虚多汗者慎服。', '【后下】出锅前5分钟放入，防挥发油逸失')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('菊花', 'Juhua', '["杭菊", "贡菊", "滁菊", "白菊花", "黄菊花"]'::jsonb, '微寒', '甘、苦', '归肺、肝经', '无毒', 10.0, '5~10g', '散风清热，平肝明目，清热解毒', '气虚胃寒、食少泄泻者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('石膏', 'Shigao', '["生石膏", "煅石膏"]'::jsonb, '大寒', '辛、甘', '归肺、胃经', '无毒', 60.0, '15~60g', '清热泻火，除烦止渴', '脾胃虚寒及血虚阴虚发热者忌服。', '【先煎】矿物类宜打碎先煎 20~30 分钟')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('知母', 'Zhimu', '["盐知母", "炒知母"]'::jsonb, '寒', '苦、甘', '归肺、胃、肾经', '无毒', 12.0, '6~12g', '清热泻火，滋阴润燥', '脾虚便溏者禁服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('栀子', 'Zhizi', '["山栀", "焦栀子", "栀子炭"]'::jsonb, '寒', '苦', '归心、肺、三焦经', '无毒', 10.0, '6~10g', '泻火除烦，清热利湿，凉血解毒；外用消肿止痛', '脾虚便溏者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('黄芩', 'Huangqin', '["条芩", "子芩", "酒黄芩", "黄芩炭"]'::jsonb, '寒', '苦', '归肺、胆、脾、大肠、小肠经', '无毒', 10.0, '3~10g', '清热燥湿，泻火解毒，止血，安胎', '脾胃虚寒、苦寒伤胃者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('黄连', 'Huanglian', '["味连", "雅连", "云连", "酒黄连", "姜黄连"]'::jsonb, '寒', '苦', '归心、脾、胃、肝、胆、大肠经', '无毒', 5.0, '2~5g', '清热燥湿，泻火解毒', '大苦大寒，过服久服易伤脾胃，阴虚津伤者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('黄柏', 'Huangbai', '["关黄柏", "川黄柏", "盐黄柏"]'::jsonb, '寒', '苦', '归肾、膀胱经', '无毒', 12.0, '3~12g', '湿热泻痢，黄疸尿赤，带下阴痒，热淋涩痛，脚气痿躄，骨蒸劳热，盗汗，遗精', '脾胃虚寒者忌服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('苦参', 'Kushen', '["苦骨", "苦参片"]'::jsonb, '寒', '苦', '归心、肝、胃、大肠、膀胱经', '无毒', 10.0, '4.5~9g', '清热燥湿，杀虫，利尿', '【十八反】：反藜芦！脾胃虚寒者忌服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('金银花', 'Jinyinhua', '["双花", "二宝花", "银花"]'::jsonb, '寒', '甘', '归肺、心、胃经', '无毒', 15.0, '6~15g', '清热解毒，疏散风热', '脾胃虚寒及气虚疮疡脓清者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('连翘', 'Lianqiao', '["青翘", "老翘"]'::jsonb, '微寒', '苦', '归肺、心、小肠经', '无毒', 15.0, '6~15g', '清热解毒，消肿散结，疏散风热', '脾胃虚弱、气虚便溏者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('地黄', 'Dihuang', '["生地黄", "生地", "干地黄"]'::jsonb, '寒', '甘、苦', '归心、肝、肾经', '无毒', 30.0, '10~30g', '清热凉血，养阴生津', '脾虚湿滞、腹满便溏者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('赤芍', 'Chishao', '["赤芍药", "草芍药"]'::jsonb, '微寒', '苦', '归肝经', '无毒', 12.0, '6~12g', '清热凉血，散瘀止痛', '【十八反】：反藜芦！血虚无瘀及虚寒便溏者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('玄参', 'Xuanshen', '["元参", "黑玄参"]'::jsonb, '微寒', '甘、苦、咸', '归肺、胃、肾经', '无毒', 15.0, '9~15g', '清热凉血，滋阴降火，解毒散结', '【十八反】：反藜芦！脾胃虚寒便溏者禁服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('大黄', 'Dahuang', '["生大黄", "熟大黄", "酒大黄", "大黄炭"]'::jsonb, '寒', '苦', '归脾、胃、大肠、肝、心包经', '无毒', 15.0, '3~15g', '泻下攻积，清热泻火，凉血解毒，逐瘀通经', '孕妇、产后、月经期及脾胃虚寒者慎用或禁用。', '泻下【后下】或开水泡服；逐瘀活血宜酒炙')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('芒硝', 'Mangxiao', '["朴硝", "玄明粉", "风化硝"]'::jsonb, '大寒', '咸、苦', '归胃、大肠经', '无毒', 10.0, '6~10g', '泻下通便，润燥软坚，清火消肿', '【十九畏】：芒硝畏三棱！孕妇禁用。', '【冲服】不入煎，溶于煎好的汤药中')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('甘遂', 'Gansui', '["遂草", "主水"]'::jsonb, '寒', '苦', '归肺、肾、大肠经', '有毒', 1.5, '0.5~1.5g', '泻水逐饮，消肿散结', '【十八反】：严禁与甘草同用！同用诱发剧烈肠坏死与中毒。孕妇及体虚者禁用。', '多入丸散服，极少入煎剂')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('大戟', 'Daji', '["京大戟", "红芽大戟"]'::jsonb, '寒', '苦、辛', '归肺、脾、肾经', '有毒', 3.0, '1.5~3g', '泻水逐饮，消肿散结', '【十八反】：严禁与甘草配伍！孕妇忌服。', '多醋炙后入丸散或煎服')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('芫花', 'Yuanhua', '["醋芫花"]'::jsonb, '温', '辛、苦', '归肺、脾、肾经', '有毒', 3.0, '1.5~3g', '泻水逐饮，祛痰止咳，杀虫疗疮', '【十八反】：严禁与甘草配伍！孕妇禁用。', '多醋炙以减低毒性')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('海藻', 'Haizao', '["大海藻", "小海藻"]'::jsonb, '寒', '苦、咸', '归肝、胃、肾经', '无毒', 12.0, '6~12g', '消痰软坚，利水消肿', '【十八反】：严禁与甘草配伍！脾胃虚寒者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('巴豆', 'Badou', '["巴豆霜", "巴豆仁"]'::jsonb, '热', '辛', '归胃、大肠经', '大毒', 0.3, '0.1~0.3g (巴豆霜)', '泻下冷积，逐水退肿，祛痰利咽，外用蚀疮', '【十九畏】：巴豆畏牵牛子！大毒之品，孕妇及体弱者严禁使用。', '多制成巴豆霜入丸散')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('牵牛子', 'Qianniuzi', '["黑丑", "白丑", "二丑"]'::jsonb, '寒', '苦、辛', '归肺、肾、大肠经', '有毒', 6.0, '3~6g', '泻下逐水，去积杀虫', '【十九畏】：牵牛子畏巴豆！孕妇及虚弱者禁用。', '入丸散多研末，煎剂后下')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('附子', 'Fuzi', '["制附子", "黑顺片", "白附片", "熟附子", "淡附片"]'::jsonb, '大热', '辛、甘', '归心、脾、肾经', '有毒', 15.0, '3~15g', '回阳救逆，补火助阳，散寒止痛', '【十八反】：反半夏、瓜蒌、贝母、白蔹、白及！孕妇禁用，阴虚阳盛者忌服。超量或未久煎易致乌头碱中毒致室颤。', '【先煎】文火单独先煎 30~60 分钟至口尝无麻舌感')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('川乌', 'Chuanwu', '["制川乌", "生川乌"]'::jsonb, '热', '辛、苦', '归心、肝、脾、肾经', '大毒', 3.0, '1.5~3g (制)', '祛风除湿，温经止痛', '【十八反】：反半夏、瓜蒌、贝母、白蔹、白及！【十九畏】：川乌畏犀角（水牛角）。生品内服极慎。', '【先煎】久煎 1~2 小时破坏剧毒双酯型乌头碱')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('草乌', 'Caowu', '["制草乌", "生草乌"]'::jsonb, '热', '辛、苦', '归心、肝、脾、肾经', '大毒', 3.0, '1.5~3g (制)', '祛风除湿，温经止痛', '【十八反】：反半夏、瓜蒌、贝母、白蔹、白及！【十九畏】：草乌畏犀角（水牛角）。', '【先煎】宜久煎 1~2 小时')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('干姜', 'Ganjiang', '["白姜", "干生姜"]'::jsonb, '热', '辛', '归脾、胃、心、肺经', '无毒', 10.0, '3~10g', '温中散寒，回阳通脉，温肺化饮', '阴虚内热、血热妄行者禁服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('肉桂', 'Rougai', '["官桂", "油桂", "桂皮", "桂心"]'::jsonb, '大热', '辛、甘', '归肾、脾、心、肝经', '无毒', 5.0, '1~5g', '补火助阳，引火归元，散寒止痛，温通经脉', '【十九畏】：官桂（肉桂）畏赤石脂！阴虚火旺、血热妄行及孕妇忌服。', '【后下】或研粉冲服')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('丁香', 'Dingxiang', '["公丁香", "母丁香", "丁香花"]'::jsonb, '温', '辛', '归脾、胃、肺、肾经', '无毒', 3.0, '1~3g', '温中降逆，补肾助阳', '【十九畏】：丁香莫与郁金见（丁香畏郁金）！热病及阴虚内热者忌服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('赤石脂', 'Chishizhi', '["红石头", "赤脂"]'::jsonb, '温', '甘、酸、涩', '归胃、大肠经', '无毒', 20.0, '9~20g', '涩肠止泻，止血，生肌敛疮', '【十九畏】：赤石脂畏肉桂（官桂）！', '【先煎】矿物类宜打碎先煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('郁金', 'Yujin', '["广郁金", "川郁金", "黄郁金"]'::jsonb, '寒', '辛、苦', '归肝、心、肺经', '无毒', 12.0, '5~12g', '活血止痛，行气解郁，清心凉血，利胆退黄', '【十九畏】：郁金畏丁香！孕妇慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('半夏', 'Banxia', '["法半夏", "姜半夏", "清半夏", "生半夏"]'::jsonb, '温', '辛', '归脾、胃、肺经', '有毒', 9.0, '3~9g (制)', '燥湿化痰，降逆止呕，消痞散结', '【十八反】：反乌头、附子！生品刺激喉咙有毒，必须炮制。阴虚燥咳、津伤口渴者忌用。', '内服多用法半夏、姜半夏；若生半夏须久煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('瓜蒌', 'Gualou', '["全瓜蒌", "栝楼", "瓜蒌仁", "瓜蒌皮", "天花粉"]'::jsonb, '微寒', '甘、微苦', '归肺、胃、大肠经', '无毒', 30.0, '9~15g (全瓜蒌可至30g)', '清热涤痰，宽胸散结，润燥滑肠', '【十八反】：反乌头、附子！脾虚便溏者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('贝母', 'Beimu', '["川贝母", "浙贝母", "川贝", "浙贝"]'::jsonb, '微寒', '苦、甘', '归肺、心经', '无毒', 10.0, '3~10g (研粉冲服1~2g)', '清热化痰，润肺止咳，散结消肿', '【十八反】：反乌头、附子！寒痰湿痰者慎服。', '川贝多研粉冲服，浙贝多入煎剂')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('白蔹', 'Bailian', '["山地瓜", "白根"]'::jsonb, '微寒', '苦、辛', '归心、胃经', '无毒', 10.0, '4.5~10g', '清热解毒，消痈散结，生肌止痛', '【十八反】：反乌头、附子！脾胃虚寒者禁服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('白及', 'Baiji', '["白芨", "连及草"]'::jsonb, '微寒', '苦、甘、涩', '归肺、肝、胃经', '无毒', 15.0, '6~15g', '收敛止血，消肿生肌', '【十八反】：反乌头、附子！外感咳血、肺痈初起有表邪者忌服。', '常规水煎或研末冲服')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('人参', 'Renshen', '["生晒参", "红参", "高丽参", "野山参"]'::jsonb, '微温', '甘、微苦', '归脾、肺、心、肾经', '无毒', 10.0, '3~9g (急救脱证可至15~30g)', '大补元气，复脉固脱，补脾益肺，生津养血，安神益智', '【十八反】：反藜芦！【十九畏】：畏五灵脂！实证、热证及正气不虚者禁服。不宜与茶、萝卜同服。', '【另煎】文火单独煎 2~3 小时兑服')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('党参', 'Dangshen', '["潞党参", "西党参", "炒党参"]'::jsonb, '平', '甘', '归脾、肺经', '无毒', 30.0, '9~30g', '健脾益肺，养血生津', '【十八反】：诸参反藜芦（反藜芦）！实热证不宜用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('黄芪', 'Huangqi', '["北芪", "绵黄芪", "炙黄芪"]'::jsonb, '微温', '甘', '归肺、脾经', '无毒', 60.0, '9~30g (重症可用至60g)', '补气升阳，固表止汗，利水消肿，生津养血，行滞通痹，托毒排脓，敛疮生肌', '表实邪盛、内有积滞、阴虚阳亢及热毒未清者忌用。', '补气升阳宜炙用，利水排脓宜生用')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('白术', 'Baizhu', '["炒白术", "焦白术", "生白术"]'::jsonb, '温', '苦、甘', '归脾、胃经', '无毒', 15.0, '6~15g', '健脾益气，燥湿利水，止汗，安胎', '阴虚内热、津液亏耗者慎用。', '健脾和胃宜炒用，燥湿利水宜生用')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('甘草', 'Gancao', '["炙甘草", "生甘草", "粉甘草"]'::jsonb, '平', '甘', '归心、肺、脾、胃经', '无毒', 10.0, '2~10g', '补脾益气，清热解毒，祛痰止咳，缓急止痛，调和诸药', '【十八反】：严禁与甘遂、大戟、海藻、芫花同用！湿盛胀满、水肿者忌服。', '常规水煎，清热解毒生用，补中益气炙用')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('当归', 'Danggui', '["秦归", "当归身", "当归尾", "酒当归"]'::jsonb, '温', '甘、辛', '归肝、心、脾经', '无毒', 15.0, '6~12g', '补血活血，调经止痛，润肠通便', '湿盛中满、大便溏泄者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('白芍', 'Baishao', '["生白芍", "炒白芍", "酒白芍"]'::jsonb, '微寒', '苦、酸', '归肝、脾经', '无毒', 15.0, '6~15g', '养血调经，敛阴止汗，柔肝止痛，平抑肝阳', '【十八反】：反藜芦！虚寒腹泻者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('熟地黄', 'Shudihuang', '["熟地", "熟地炭"]'::jsonb, '微温', '甘', '归肝、肾经', '无毒', 30.0, '9~15g (重症可用至30g)', '补血滋阴，益精填髓', '性滋腻，脾虚食少便溏、痰湿内阻者慎服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('丹参', 'Danshen', '["紫丹参", "酒丹参"]'::jsonb, '微寒', '苦', '归心、肝经', '无毒', 30.0, '9~15g', '活血祛瘀，通经止痛，清心除烦，凉血消痈', '【十八反】：反藜芦！月经过多及无瘀血者慎用。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('沙参', 'Shashen', '["北沙参", "南沙参"]'::jsonb, '微寒', '甘、微苦', '归肺、胃经', '无毒', 15.0, '9~15g', '养阴清肺，益胃生津', '【十八反】：反藜芦！风寒咳嗽及脾胃虚寒便溏者禁服。', '常规水煎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('藜芦', 'Lilu', '["葱芦", "山葱"]'::jsonb, '寒', '辛、苦', '归肺、胃、肝经', '有毒', 0.6, '0.3~0.6g', '涌吐风痰，杀虫疗疮', '【十八反】：反诸参（人参、党参、沙参、丹参、玄参、苦参）、细辛、芍药！体虚失血及孕妇禁用。', '内服多入丸散，用量极慎')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('五灵脂', 'Wulingzhi', '["灵脂", "生灵脂", "醋灵脂"]'::jsonb, '温', '苦、咸、甘', '归肝经', '无毒', 10.0, '3~10g', '活血止痛，化瘀止血', '【十九畏】：五灵脂畏人参！孕妇慎用。', '【包煎】化瘀止痛宜生用，止血宜炒用')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('旋覆花', 'Xuanfuhua', '["金沸草花", "金钱花"]'::jsonb, '微温', '苦、辛、咸', '归肺、脾、胃、大肠经', '无毒', 10.0, '3~10g', '降气化痰，降逆止呕', '阴虚劳嗽、津伤燥咳者慎用。', '【包煎】绒毛易脱落刺激咽喉致剧咳，必须用纱布包扎后煎煮')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('阿胶', 'Ejiao', '["驴皮胶", "阿胶珠"]'::jsonb, '平', '甘', '归肺、肝、肾经', '无毒', 15.0, '3~9g (大剂量至15g)', '补血滋阴，润燥，止血', '脾胃虚弱、消化不良者慎用。', '【烊化】入药不入群药同煎，宜在煎好的温药汤中冲服搅拌溶化')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('砂仁', 'Sharen', '["阳春砂", "春砂仁"]'::jsonb, '温', '辛', '归脾、胃、肾经', '无毒', 6.0, '3~6g', '化湿开胃，温脾止泻，理气安胎', '阴虚有热者忌服。', '【后下】打碎后在起锅前 5~10 分钟下入')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;

INSERT INTO tcm_herb_dict (herb_name, pinyin, aliases, nature, flavor, meridians, toxicity_level, max_dosage, dosage_range, efficacy, contraindications, decoction_method)
VALUES ('车前子', 'Cheqianzi', '["炒车前子", "盐车前子"]'::jsonb, '微寒', '甘', '归肝、肾、肺、小肠经', '无毒', 15.0, '9~15g', '清热利尿通淋，渗湿止泻，明目，祛痰', '肾虚精滑及内无湿热者慎用。', '【包煎】种子细小且富含黏质，易附着锅底糊化')
ON CONFLICT (herb_name) DO UPDATE SET
  max_dosage = EXCLUDED.max_dosage,
  efficacy = EXCLUDED.efficacy,
  contraindications = EXCLUDED.contraindications;


-- 经典名方数据入库 --

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('补中益气汤', '李东垣《脾胃论》', '李东垣', '补益剂 · 补气', '[{"name": "黄芪", "dose": 15.0, "unit": "g", "role": "君药"}, {"name": "人参", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "白术", "dose": 10.0, "unit": "g", "role": "臣药"}, {"name": "炙甘草", "dose": 6.0, "unit": "g", "role": "使药"}, {"name": "当归", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "陈皮", "dose": 6.0, "unit": "g", "role": "佐药"}, {"name": "升麻", "dose": 3.0, "unit": "g", "role": "佐药"}, {"name": "柴胡", "dose": 3.0, "unit": "g", "role": "佐药"}]'::jsonb, '脾虚气陷证。症见体倦乏力、食少腹胀、久泻脱肛、子宫脱垂、气虚发热。', '黄芪', '人参, 白术', '当归, 陈皮, 升麻, 柴胡', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('麻黄汤', '张仲景《伤寒论》', '张仲景', '解表剂 · 辛温解表', '[{"name": "麻黄", "dose": 9.0, "unit": "g", "role": "君药"}, {"name": "桂枝", "dose": 6.0, "unit": "g", "role": "臣药"}, {"name": "杏仁", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "炙甘草", "dose": 3.0, "unit": "g", "role": "使药"}]'::jsonb, '外感风寒表实证。恶寒发热，头痛身疼，无汗而喘，脉浮紧。', '麻黄', '桂枝', '杏仁', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('桂枝汤', '张仲景《伤寒论》', '张仲景', '解表剂 · 辛温解表', '[{"name": "桂枝", "dose": 9.0, "unit": "g", "role": "君药"}, {"name": "白芍", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "生姜", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "大枣", "dose": 12.0, "unit": "g", "role": "佐药"}, {"name": "炙甘草", "dose": 6.0, "unit": "g", "role": "使药"}]'::jsonb, '外感风寒表虚证。头痛发热，汗出恶风，鼻鸣干呕，苔薄白，脉浮缓。', '桂枝', '白芍', '生姜, 大枣', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('四逆汤', '张仲景《伤寒论》', '张仲景', '温里剂 · 回阳救逆', '[{"name": "附子", "dose": 12.0, "unit": "g", "role": "君药 (先煎)"}, {"name": "干姜", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "炙甘草", "dose": 6.0, "unit": "g", "role": "佐使药"}]'::jsonb, '少阴病阳虚阴盛证。四肢厥逆，神衰欲寐，下利清谷，脉微欲绝。', '附子', '干姜', '炙甘草', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('当归补血汤', '李东垣《内外伤辨惑论》', '李东垣', '补益剂 · 补血', '[{"name": "黄芪", "dose": 30.0, "unit": "g", "role": "君药 (一两)"}, {"name": "当归", "dose": 6.0, "unit": "g", "role": "臣药 (二钱)"}]'::jsonb, '血虚发热证。肌热面赤，烦渴引饮，脉洪大而虚，重按无力。黄芪当归 5:1 经典配伍。', '黄芪', '当归', '', '')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('四君子汤', '《太平惠民和剂局方》', '宋代太医院', '补益剂 · 补气', '[{"name": "人参", "dose": 9.0, "unit": "g", "role": "君药"}, {"name": "白术", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "茯苓", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "炙甘草", "dose": 6.0, "unit": "g", "role": "使药"}]'::jsonb, '脾胃气虚基础方。面色萎黄，语声低微，四肢倦怠，食少便溏，舌淡苔白，脉沉细无力。', '人参', '白术', '茯苓', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('四物汤', '《仙授理伤续断秘方》', '蔺道人', '补益剂 · 补血', '[{"name": "熟地黄", "dose": 12.0, "unit": "g", "role": "君药"}, {"name": "当归", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "白芍", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "川芎", "dose": 6.0, "unit": "g", "role": "使药"}]'::jsonb, '营血虚滞证。面色无华，头晕目眩，心悸怔忡，妇人月经不调，量少腹痛，舌淡，脉细弦。', '熟地黄', '当归', '白芍', '川芎')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('六味地黄丸', '钱乙《小儿药证直诀》', '钱乙', '补益剂 · 补阴', '[{"name": "熟地黄", "dose": 24.0, "unit": "g", "role": "君药"}, {"name": "山茱萸", "dose": 12.0, "unit": "g", "role": "臣药"}, {"name": "山药", "dose": 12.0, "unit": "g", "role": "臣药"}, {"name": "泽泻", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "茯苓", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "牡丹皮", "dose": 9.0, "unit": "g", "role": "佐药"}]'::jsonb, '肝肾阴虚证。腰膝酸软，头晕目眩，耳鸣耳聋，盗汗遗精，骨蒸潮热，手足心热，舌红少苔，脉沉细数。三补三泻经典构型。', '熟地黄', '山茱萸, 山药', '泽泻, 茯苓, 牡丹皮', '')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('逍遥散', '《太平惠民和剂局方》', '宋代太医院', '和解剂 · 调和肝脾', '[{"name": "柴胡", "dose": 9.0, "unit": "g", "role": "君药"}, {"name": "当归", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "白芍", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "白术", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "茯苓", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "炙甘草", "dose": 4.5, "unit": "g", "role": "使药"}]'::jsonb, '肝郁血虚脾弱证。两胁作痛，头痛目眩，神疲食少，月经不调，乳房胀痛，脉弦而虚。', '柴胡', '当归, 白芍', '白术, 茯苓', '炙甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;

INSERT INTO tcm_formula_catalog (formula_name, source_book, author, category, composition, indications, monarch_herb, minister_herb, assistant_herb, guide_herb)
VALUES ('血府逐瘀汤', '王清任《医林改错》', '王清任', '理血剂 · 活血祛瘀', '[{"name": "桃仁", "dose": 12.0, "unit": "g", "role": "君药"}, {"name": "红花", "dose": 9.0, "unit": "g", "role": "君药"}, {"name": "当归", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "生地黄", "dose": 9.0, "unit": "g", "role": "臣药"}, {"name": "川芎", "dose": 5.0, "unit": "g", "role": "臣药"}, {"name": "赤芍", "dose": 6.0, "unit": "g", "role": "臣药"}, {"name": "牛膝", "dose": 9.0, "unit": "g", "role": "佐药"}, {"name": "柴胡", "dose": 3.0, "unit": "g", "role": "佐药"}, {"name": "桔梗", "dose": 5.0, "unit": "g", "role": "使药"}, {"name": "枳壳", "dose": 6.0, "unit": "g", "role": "佐药"}, {"name": "甘草", "dose": 3.0, "unit": "g", "role": "使药"}]'::jsonb, '胸中血瘀证。胸痛、头痛，日久不愈，痛如针刺而有定处，心悸怔忡，夜不能寐，急躁易怒，舌质暗红有瘀斑，脉涩或弦紧。', '桃仁, 红花', '当归, 生地黄, 川芎, 赤芍', '牛膝, 柴胡, 枳壳', '桔梗, 甘草')
ON CONFLICT (formula_name) DO UPDATE SET
  indications = EXCLUDED.indications,
  composition = EXCLUDED.composition;
