package com.example.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", "English", false),
    THAI("th", "Thai", "ไทย", false),
    HEBREW("he", "Hebrew", "עברית", true),
    JAPANESE("ja", "Japanese", "日本語", false),
    KOREAN("ko", "Korean", "한국어", false),
    CHINESE("zh", "Chinese", "中文", false),
    RUSSIAN("ru", "Russian", "Русский", false),
    ARABIC("ar", "Arabic", "العربية", true),
    SPANISH("es", "Spanish", "Español", false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            val clean = code.lowercase().take(2)
            return values().firstOrNull { it.code == clean } ?: ENGLISH
        }
    }
}

object Localization {
    private val strings = mapOf(
        "nav_translate" to mapOf(
            AppLanguage.ENGLISH to "Translate",
            AppLanguage.THAI to "แปลภาษา",
            AppLanguage.HEBREW to "תרגם",
            AppLanguage.JAPANESE to "翻訳",
            AppLanguage.KOREAN to "번역",
            AppLanguage.CHINESE to "翻译",
            AppLanguage.RUSSIAN to "Перевод",
            AppLanguage.ARABIC to "ترجمة",
            AppLanguage.SPANISH to "Traducir"
        ),
        "nav_chat" to mapOf(
            AppLanguage.ENGLISH to "AI Chat",
            AppLanguage.THAI to "แชท AI",
            AppLanguage.HEBREW to "צ'אט AI",
            AppLanguage.JAPANESE to "AIチャット",
            AppLanguage.KOREAN to "AI 채팅",
            AppLanguage.CHINESE to "AI 聊天",
            AppLanguage.RUSSIAN to "AI Чат",
            AppLanguage.ARABIC to "دردشة AI",
            AppLanguage.SPANISH to "Chat AI"
        ),
        "nav_voice" to mapOf(
            AppLanguage.ENGLISH to "Oral AI",
            AppLanguage.THAI to "แปลด้วยเสียง",
            AppLanguage.HEBREW to "תרגום קולי",
            AppLanguage.JAPANESE to "音声AI",
            AppLanguage.KOREAN to "음성 AI",
            AppLanguage.CHINESE to "语音 AI",
            AppLanguage.RUSSIAN to "Голос AI",
            AppLanguage.ARABIC to "صوت AI",
            AppLanguage.SPANISH to "Oral AI"
        ),
        "nav_camera" to mapOf(
            AppLanguage.ENGLISH to "Lens",
            AppLanguage.THAI to "เลนส์กล้อง",
            AppLanguage.HEBREW to "עדשה",
            AppLanguage.JAPANESE to "レンズ",
            AppLanguage.KOREAN to "렌즈",
            AppLanguage.CHINESE to "镜头",
            AppLanguage.RUSSIAN to "Камера",
            AppLanguage.ARABIC to "عدسة",
            AppLanguage.SPANISH to "Cámara"
        ),
        "nav_archive" to mapOf(
            AppLanguage.ENGLISH to "Archive",
            AppLanguage.THAI to "บันทึกข้อมูล",
            AppLanguage.HEBREW to "ארכיון",
            AppLanguage.JAPANESE to "履歴保存",
            AppLanguage.KOREAN to "보관함",
            AppLanguage.CHINESE to "归档",
            AppLanguage.RUSSIAN to "Архив",
            AppLanguage.ARABIC to "الأرشيف",
            AppLanguage.SPANISH to "Archivo"
        ),
        "premium_ai" to mapOf(
            AppLanguage.ENGLISH to "PREMIUM AI",
            AppLanguage.THAI to "แปลภาษาพรีเมียม",
            AppLanguage.HEBREW to "AI פרימיום",
            AppLanguage.JAPANESE to "プレミアム AI",
            AppLanguage.KOREAN to "프리미엄 AI",
            AppLanguage.CHINESE to "高级 AI",
            AppLanguage.RUSSIAN to "ПРЕМИУМ AI",
            AppLanguage.ARABIC to "ذكاء اصطناعي مميز",
            AppLanguage.SPANISH to "AI PREMIUM"
        ),
        "mode_ai_natural" to mapOf(
            AppLanguage.ENGLISH to "AI Natural",
            AppLanguage.THAI to "ภาษาธรรมชาติ",
            AppLanguage.HEBREW to "AI טבעי",
            AppLanguage.JAPANESE to "AI自然体",
            AppLanguage.KOREAN to "AI 자연스러움",
            AppLanguage.CHINESE to "AI 自然",
            AppLanguage.RUSSIAN to "AI Естественный",
            AppLanguage.ARABIC to "ذكاء طبيعي",
            AppLanguage.SPANISH to "AI Natural"
        ),
        "mode_direct" to mapOf(
            AppLanguage.ENGLISH to "Direct",
            AppLanguage.THAI to "ตรงตัว",
            AppLanguage.HEBREW to "תרגום ישיר",
            AppLanguage.JAPANESE to "直訳",
            AppLanguage.KOREAN to "직역",
            AppLanguage.CHINESE to "直译",
            AppLanguage.RUSSIAN to "Прямой",
            AppLanguage.ARABIC to "مباشر",
            AppLanguage.SPANISH to "Directo"
        ),
        "splash_tagline" to mapOf(
            AppLanguage.ENGLISH to "Cognitive AI Language Translator",
            AppLanguage.THAI to "ระบบแปลภาษาด้วยปัญญาประดิษฐ์อัจฉริยะ",
            AppLanguage.HEBREW to "מתרגם שפה קוגניטיבי AI",
            AppLanguage.JAPANESE to "認知型 AI 言語翻訳アプリ",
            AppLanguage.KOREAN to "인지형 AI 언어 번역기",
            AppLanguage.CHINESE to "认知 AI 语言翻译器",
            AppLanguage.RUSSIAN to "Когнитивный AI Переводчик",
            AppLanguage.ARABIC to "مترجم اللغة المعرفي بالذكاء الاصطناعي",
            AppLanguage.SPANISH to "Traductor de Idiomas AI Cognitivo"
        ),
        "splash_status" to mapOf(
            AppLanguage.ENGLISH to "Connecting to Neural Core...",
            AppLanguage.THAI to "กำลังเชื่อมต่อกับโครงข่ายประสาทเทียม...",
            AppLanguage.HEBREW to "מתחבר לליבה הניורונית...",
            AppLanguage.JAPANESE to "ニューラルコアに接続中...",
            AppLanguage.KOREAN to "뉴럴 코어에 연결 중...",
            AppLanguage.CHINESE to "连接到神经核心...",
            AppLanguage.RUSSIAN to "Подключение к нейронному ядру...",
            AppLanguage.ARABIC to "جاري الاتصال بالنواة العصبية...",
            AppLanguage.SPANISH to "Conectando al Núcleo Neuronal..."
        ),
        "splash_ready" to mapOf(
            AppLanguage.ENGLISH to "System Ready",
            AppLanguage.THAI to "ระบบพร้อมใช้งาน",
            AppLanguage.HEBREW to "המערכת מוכנה",
            AppLanguage.JAPANESE to "システム準備完了",
            AppLanguage.KOREAN to "시스템 준비됨",
            AppLanguage.CHINESE to "系统已就绪",
            AppLanguage.RUSSIAN to "Система готова",
            AppLanguage.ARABIC to "النظام جاهز",
            AppLanguage.SPANISH to "Sistema Listo"
        ),
        "source_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Tap to enter text...",
            AppLanguage.THAI to "แตะเพื่อป้อนข้อความ...",
            AppLanguage.HEBREW to "הקש להקלדת טקסט...",
            AppLanguage.JAPANESE to "タップしてテキストを入力...",
            AppLanguage.KOREAN to "탭하여 텍스트 입력...",
            AppLanguage.CHINESE to "点击输入文本...",
            AppLanguage.RUSSIAN to "Нажмите для ввода текста...",
            AppLanguage.ARABIC to "انقر لإدخال النص...",
            AppLanguage.SPANISH to "Toca para ingresar texto..."
        ),
        "settings_pref" to mapOf(
            AppLanguage.ENGLISH to "System Preferences",
            AppLanguage.THAI to "ตั้งค่าระบบ",
            AppLanguage.HEBREW to "העדפות מערכת",
            AppLanguage.JAPANESE to "システム設定",
            AppLanguage.KOREAN to "시스템 설정",
            AppLanguage.CHINESE to "系统首选项",
            AppLanguage.RUSSIAN to "Настройки системы",
            AppLanguage.ARABIC to "تفضيلات النظام",
            AppLanguage.SPANISH to "Preferencias del Sistema"
        ),
        "settings_descr" to mapOf(
            AppLanguage.ENGLISH to "Fine-tune cognitive and vocal parameters",
            AppLanguage.THAI to "ปรับแต่งพารามิเตอร์การประมวลผลและเสียงพูด",
            AppLanguage.HEBREW to "כוונן פרמטרים קוגניטיביים וקוליים",
            AppLanguage.JAPANESE to "認知および音声パラメータの微調整",
            AppLanguage.KOREAN to "인지 학습 및 목소리 출력 옵션 미세 조정",
            AppLanguage.CHINESE to "微调认知和语音参数",
            AppLanguage.RUSSIAN to "Точная настройка когнитивных и вокальных параметров",
            AppLanguage.ARABIC to "ضبط المعلمات المعرفية والصوتية بدقة",
            AppLanguage.SPANISH to "Ajustar parámetros cognitivos y vocales"
        ),
        "vocal_speech_rate" to mapOf(
            AppLanguage.ENGLISH to "Vocal Speech Rate",
            AppLanguage.THAI to "ความเร็วของเสียงพูด",
            AppLanguage.HEBREW to "קצב דיבור קולי",
            AppLanguage.JAPANESE to "発話速度",
            AppLanguage.KOREAN to "목소리 재생 속도",
            AppLanguage.CHINESE to "语音语速",
            AppLanguage.RUSSIAN to "Скорость речи",
            AppLanguage.ARABIC to "معدل سرعة الصوت",
            AppLanguage.SPANISH to "Velocidad de Voz"
        ),
        "vocal_speech_descr" to mapOf(
            AppLanguage.ENGLISH to "Adjust the oral reproduction pace of translate speakers.",
            AppLanguage.THAI to "ปรับแต่งจังหวะความเร็วในการออกเสียงแปลภาษา",
            AppLanguage.HEBREW to "כוונן את קצב הפקת הדיבור של המתרגם.",
            AppLanguage.JAPANESE to "翻訳話者の音声再生速度を調整します。",
            AppLanguage.KOREAN to "번역된 텍스트의 목소리 재생 속도를 조절합니다.",
            AppLanguage.CHINESE to "调整翻译发音的播放速度。",
            AppLanguage.RUSSIAN to "Настройте темп воспроизведения речи переводчика.",
            AppLanguage.ARABIC to "اضبط وتيرة إعادة إنتاج الصوت للمتحدثين المترجمين.",
            AppLanguage.SPANISH to "Ajustar el ritmo de reproducción oral de los traductores."
        ),
        "offline_cache" to mapOf(
            AppLanguage.ENGLISH to "Persistent Offline Cache",
            AppLanguage.THAI to "แคชออฟไลน์ถาวร",
            AppLanguage.HEBREW to "מטמון לא מקוון קבוע",
            AppLanguage.JAPANESE to "永続的オフラインキャッシュ",
            AppLanguage.KOREAN to "영구 오프라인 캐시 저장",
            AppLanguage.CHINESE to "持久化离线缓存",
            AppLanguage.RUSSIAN to "Постоянный офлайн-кеш",
            AppLanguage.ARABIC to "مخزن مؤقت دائم بدون اتصال",
            AppLanguage.SPANISH to "Caché Permanente sin Conexión"
        ),
        "offline_cache_descr" to mapOf(
            AppLanguage.ENGLISH to "Commit logs directly into local private sandbox",
            AppLanguage.THAI to "จัดเก็บบันทึกประวัติการแปลลงในเครื่องส่วนตัวโดยตรง",
            AppLanguage.HEBREW to "שמור יומנים ישירות בתיבת החול הפרטית המקומית",
            AppLanguage.JAPANESE to "ログをローカルのプライベートサンドボックスに直接保存します",
            AppLanguage.KOREAN to "기록 로그를 로컬 전용 샌드박스에 바로 저장합니다",
            AppLanguage.CHINESE to "直接将日志提交到本地私有沙盒",
            AppLanguage.RUSSIAN to "Сохраняйте логи прямо в локальную песочницу",
            AppLanguage.ARABIC to "حفظ السجلات مباشرة في البيئة التجريبية الخاصة المحلية",
            AppLanguage.SPANISH to "Confirmar registros directamente en el entorno local privado"
        ),
        "ai_core_details" to mapOf(
            AppLanguage.ENGLISH to "Linguistic AI Core Details",
            AppLanguage.THAI to "รายละเอียดแกนสมองกล AI",
            AppLanguage.HEBREW to "פרטי ליבת ה-AI הלשונית",
            AppLanguage.JAPANESE to "言語 AI コアの詳細",
            AppLanguage.KOREAN to "언어학적 AI 코어 상세 정보",
            AppLanguage.CHINESE to "语言 AI 核心详细信息",
            AppLanguage.RUSSIAN to "Детали лингвистического ядра AI",
            AppLanguage.ARABIC to "تفاصيل نواة الذكاء الاصطناعي اللغوي",
            AppLanguage.SPANISH to "Detalles del Núcleo IA Lingüística"
        ),
        "engine_model" to mapOf(
            AppLanguage.ENGLISH to "Translating Engine Model:",
            AppLanguage.THAI to "รุ่นโมเดลแปลภาษา:",
            AppLanguage.HEBREW to "מודל מנוע התרגום:",
            AppLanguage.JAPANESE to "翻訳エンジンモデル:",
            AppLanguage.KOREAN to "번역 엔진 모델:",
            AppLanguage.CHINESE to "翻译引擎模型：",
            AppLanguage.RUSSIAN to "Модель движка перевода:",
            AppLanguage.ARABIC to "نموذج محرك الترجمة:",
            AppLanguage.SPANISH to "Modelo del Motor de Traducción:"
        ),
        "api_service_key" to mapOf(
            AppLanguage.ENGLISH to "API Service Key Protocol:",
            AppLanguage.THAI to "โปรโตคอลคีย์บริการ API:",
            AppLanguage.HEBREW to "פרוטוקול מפתח שירות API:",
            AppLanguage.JAPANESE to "APIサービスキープロトコル:",
            AppLanguage.KOREAN to "API 서비스 키 프로토콜:",
            AppLanguage.CHINESE to "API 服务密钥协议：",
            AppLanguage.RUSSIAN to "Протокол ключа службы API:",
            AppLanguage.ARABIC to "بروتوكول مفتاح خدمة API:",
            AppLanguage.SPANISH to "Protocolo de Clave del Servicio API:"
        ),
        "software_version" to mapOf(
            AppLanguage.ENGLISH to "Software Architecture Version:",
            AppLanguage.THAI to "เวอร์ชันสถาปัตยกรรมซอฟต์แวร์:",
            AppLanguage.HEBREW to "גרסת ארכיקטורת תוכנה:",
            AppLanguage.JAPANESE to "ソフトウェアアーキテクチャバージョン:",
            AppLanguage.KOREAN to "소프트웨어 아키텍처 버전:",
            AppLanguage.CHINESE to "软件架构版本：",
            AppLanguage.RUSSIAN to "Версия архитектуры ПО:",
            AppLanguage.ARABIC to "إصدار بنية البرنامج:",
            AppLanguage.SPANISH to "Versión de la Arquitectura de Software:"
        ),
        "recent_logs" to mapOf(
            AppLanguage.ENGLISH to "Recent Logs",
            AppLanguage.THAI to "ประวัติล่าสุด",
            AppLanguage.HEBREW to "יומנים אחרונים",
            AppLanguage.JAPANESE to "履歴ログ",
            AppLanguage.KOREAN to "최근 기록",
            AppLanguage.CHINESE to "最近记录",
            AppLanguage.RUSSIAN to "Недавние логи",
            AppLanguage.ARABIC to "السجلات الأخيرة",
            AppLanguage.SPANISH to "Registros Recientes"
        ),
        "preferences" to mapOf(
            AppLanguage.ENGLISH to "Preferences",
            AppLanguage.THAI to "การปรับแต่ง",
            AppLanguage.HEBREW to "העדפות",
            AppLanguage.JAPANESE to "環境設定",
            AppLanguage.KOREAN to "환경 설정",
            AppLanguage.CHINESE to "偏好设置",
            AppLanguage.RUSSIAN to "Параметры",
            AppLanguage.ARABIC to "التفضيلات",
            AppLanguage.SPANISH to "Preferencias"
        ),
        "clear_history" to mapOf(
            AppLanguage.ENGLISH to "Clear History",
            AppLanguage.THAI to "ล้างประวัติการแปล",
            AppLanguage.HEBREW to "נקה היסטוריה",
            AppLanguage.JAPANESE to "履歴をクリア",
            AppLanguage.KOREAN to "모든 기록 삭제",
            AppLanguage.CHINESE to "清除历史",
            AppLanguage.RUSSIAN to "Очистить историю",
            AppLanguage.ARABIC to "مسح السجل",
            AppLanguage.SPANISH to "Limpiar Historial"
        ),
        "no_saved_translations" to mapOf(
            AppLanguage.ENGLISH to "No saved translations yet",
            AppLanguage.THAI to "ยังไม่มีบันทึกข้อมูลการแปลภาษา",
            AppLanguage.HEBREW to "אין עדיין תרגומים שמורים",
            AppLanguage.JAPANESE to "保存された翻訳はまだありません",
            AppLanguage.KOREAN to "저장된 번역 내역이 없습니다",
            AppLanguage.CHINESE to "尚无已保存的翻译",
            AppLanguage.RUSSIAN to "Сохраненных переводов пока нет",
            AppLanguage.ARABIC to "لا توجد ترجمات محفوظة بعد",
            AppLanguage.SPANISH to "Aún no hay traducciones guardadas"
        ),
        "start_speak" to mapOf(
            AppLanguage.ENGLISH to "Hold & Speak",
            AppLanguage.THAI to "กดค้างเพื่อทดลองพูด",
            AppLanguage.HEBREW to "החזק ודבר",
            AppLanguage.JAPANESE to "長押しして話す",
            AppLanguage.KOREAN to "누르고 말하기",
            AppLanguage.CHINESE to "按住说话",
            AppLanguage.RUSSIAN to "Удерживайте и говорите",
            AppLanguage.ARABIC to "اضغط وتحدث",
            AppLanguage.SPANISH to "Mantén presionado y habla"
        ),
        "listen_recording" to mapOf(
            AppLanguage.ENGLISH to "Listening...",
            AppLanguage.THAI to "กำลังฟังเสียงพรีเมียม...",
            AppLanguage.HEBREW to "מקשיב...",
            AppLanguage.JAPANESE to "音声待機中...",
            AppLanguage.KOREAN to "대화 청취 중...",
            AppLanguage.CHINESE to "正在聆听...",
            AppLanguage.RUSSIAN to "Прослушивание...",
            AppLanguage.ARABIC to "جاري الاستماع...",
            AppLanguage.SPANISH to "Escuchando..."
        ),
        "tap_to_listen" to mapOf(
            AppLanguage.ENGLISH to "Tap to Listen",
            AppLanguage.THAI to "แตะเพื่อทดสอบออกเสียง",
            AppLanguage.HEBREW to "הקש להאזנה",
            AppLanguage.JAPANESE to "タップして聞く",
            AppLanguage.KOREAN to "탭하여 듣기",
            AppLanguage.CHINESE to "点击倾听",
            AppLanguage.RUSSIAN to "Нажмите для прослушивания",
            AppLanguage.ARABIC to "انقر للاستماع",
            AppLanguage.SPANISH to "Toca para escuchar"
        ),
        "linguistic_expl" to mapOf(
            AppLanguage.ENGLISH to "Linguistic Explanation",
            AppLanguage.THAI to "คำอธิบายเชิงภาษากลศาสตร์",
            AppLanguage.HEBREW to "הסבר לשוני",
            AppLanguage.JAPANESE to "言語学的解説",
            AppLanguage.KOREAN to "해당 언어 구조 분석 해설",
            AppLanguage.CHINESE to "语言学深度解读",
            AppLanguage.RUSSIAN to "Лингвистическое объяснение",
            AppLanguage.ARABIC to "الشرح اللغوي",
            AppLanguage.SPANISH to "Explicación Lingüística"
        ),
        "tone_selector" to mapOf(
            AppLanguage.ENGLISH to "Adjust Expression Tone",
            AppLanguage.THAI to "ปรับระดับน้ำเสียงอารมณ์",
            AppLanguage.HEBREW to "כוונן טון הבעה",
            AppLanguage.JAPANESE to "翻訳のニュアンス表現",
            AppLanguage.KOREAN to "상황별 말투 및 표현 선택",
            AppLanguage.CHINESE to "调整表达语气",
            AppLanguage.RUSSIAN to "Настройка выразительного тона",
            AppLanguage.ARABIC to "ضبط نبرة التعبير",
            AppLanguage.SPANISH to "Ajustar Tono de Expresión"
        ),
        "natural_context" to mapOf(
            AppLanguage.ENGLISH to "Natural Context",
            AppLanguage.THAI to "แปลตามบริบทธรรมชาติตัวจริง",
            AppLanguage.HEBREW to "הקשר טבעי במיוחד",
            AppLanguage.JAPANESE to "AI文脈調整済み",
            AppLanguage.KOREAN to "AI 자연맥락 조율",
            AppLanguage.CHINESE to "自然语境",
            AppLanguage.RUSSIAN to "Естественный контекст",
            AppLanguage.ARABIC to "سياق طبيعي متقدم",
            AppLanguage.SPANISH to "Contexto Real Natural"
        ),
        "explain_btn" to mapOf(
            AppLanguage.ENGLISH to "Deep Cognitive AI Explanation",
            AppLanguage.THAI to "วิเคราะห์โครงสร้างภาษาเชิงลึก",
            AppLanguage.HEBREW to "ניתוח AI קוגניטיבי עמוק",
            AppLanguage.JAPANESE to "専門的AI言語詳細解析",
            AppLanguage.KOREAN to "딥 코그니티브 언어 구조 해석 교정",
            AppLanguage.CHINESE to "深度 AI 语法及用法解读",
            AppLanguage.RUSSIAN to "Глубокое объяснение языковой структуры",
            AppLanguage.ARABIC to "تفصيل لغوي عميق بالذكاء الاصطناعي",
            AppLanguage.SPANISH to "Explicación Detallada de IA Cognitiva"
        ),
        "explaining_active" to mapOf(
            AppLanguage.ENGLISH to "Analyzing syntactic structures...",
            AppLanguage.THAI to "กำลังจำแนกไวยากรณ์ด้วยแกนสมองกล...",
            AppLanguage.HEBREW to "מנתח מבנים תחביריים...",
            AppLanguage.JAPANESE to "高度な文法構造を解読中...",
            AppLanguage.KOREAN to "심층 구문 구조 분석 중...",
            AppLanguage.CHINESE to "正在分析句法和措辞结构...",
            AppLanguage.RUSSIAN to "Анализ синтаксической структуры...",
            AppLanguage.ARABIC to "جاري تحليل الهياكل النحوية...",
            AppLanguage.SPANISH to "Analizando estructuras sintácticas..."
        ),
        "translating_active" to mapOf(
            AppLanguage.ENGLISH to "Translating cognitive streams...",
            AppLanguage.THAI to "กำลังประมวลผลกระแสข้อมูลความรู้...",
            AppLanguage.HEBREW to "מתרגם זרמים קוגניטיביים...",
            AppLanguage.JAPANESE to "認知コグニティブデータを翻訳中...",
            AppLanguage.KOREAN to "실시간 문맥 인지 가동 중...",
            AppLanguage.CHINESE to "正在流式翻译自然语义...",
            AppLanguage.RUSSIAN to "Перевод в реальном времени...",
            AppLanguage.ARABIC to "جاري ترجمة التدفقات اللغوية...",
            AppLanguage.SPANISH to "Traduciendo flujos cognitivos..."
        ),
        "cop_success" to mapOf(
            AppLanguage.ENGLISH to "Copied to Clipboard",
            AppLanguage.THAI to "คัดลอกไปยังคลิปบอร์ดแล้วสำเร็จ",
            AppLanguage.HEBREW to "הועתק ללוח בהצלחה",
            AppLanguage.JAPANESE to "クリップボードにコピーしました",
            AppLanguage.KOREAN to "텍스트가 클립보드에 복사되었습니다",
            AppLanguage.CHINESE to "复制成功！已存入剪贴板",
            AppLanguage.RUSSIAN to "Успешно скопировано в буфер",
            AppLanguage.ARABIC to "تم النسخ إلى الحافظة بنجاح",
            AppLanguage.SPANISH to "Copiado al Portapapeles con éxito"
        ),
        "ui_language" to mapOf(
            AppLanguage.ENGLISH to "Application Interface Language",
            AppLanguage.THAI to "เลือกภาษาของการแสดงผลในแอป",
            AppLanguage.HEBREW to "שפת ממשק המשתמש באפליקציה",
            AppLanguage.JAPANESE to "アプリの操作表示言語設定",
            AppLanguage.KOREAN to "애플리케이션 인터페이스 언어 선택",
            AppLanguage.CHINESE to "应用界面语言选择",
            AppLanguage.RUSSIAN to "Язык интерфейса приложения",
            AppLanguage.ARABIC to "لغة واجهة التطبيق الرئيسية",
            AppLanguage.SPANISH to "Idioma de la Interfaz del Aplicativo"
        ),
        "ui_language_descr" to mapOf(
            AppLanguage.ENGLISH to "Switch display strings dynamically without reboot.",
            AppLanguage.THAI to "สลับเปลี่ยนภาษาทั้งหมดของหน้าจอแอปพลิเคชันได้ทันทีโดยไม่ต้องเริ่มต้นระบบใหม่",
            AppLanguage.HEBREW to "החלף מחרוזות תצוגה באופן דינמי ללא צורך בהפעלה מחדש.",
            AppLanguage.JAPANESE to "アプリ全体の表記を、再起動することなく、リアルタイムに変更します。",
            AppLanguage.KOREAN to "앱을 재설치하거나 재실행할 필요 없이 즉시 언어가 교체됩니다.",
            AppLanguage.CHINESE to "即时流式切换系统的文字和排版风格，无需重启应用。",
            AppLanguage.RUSSIAN to "Динамическое переключение языка интерфейса без перезапуска приложения.",
            AppLanguage.ARABIC to "تغيير جميع نصوص الواجهة ديناميكياً ودون الحاجة لإعادة التشغيل.",
            AppLanguage.SPANISH to "Cambia los textos de la interfaz dinámicamente sin reiniciar el aplicativo."
        ),
        "language_preview" to mapOf(
            AppLanguage.ENGLISH to "Interface Preview Indicator",
            AppLanguage.THAI to "ตัวอย่างคำพูดจำลองของหน้าจอ",
            AppLanguage.HEBREW to "מחוון תצוגה מקדימה של הממשק",
            AppLanguage.JAPANESE to "表示インターフェース確認用見本",
            AppLanguage.KOREAN to "인터페이스 다국어 미리보기 제어기",
            AppLanguage.CHINESE to "界面语言多国字形预览栏",
            AppLanguage.RUSSIAN to "Индикатор предпросмотра интерфейса",
            AppLanguage.ARABIC to "مؤشر معاينة واجهة اللغة",
            AppLanguage.SPANISH to "Indicador de Vista Previa de la Interfaz"
        ),
        "apply_lang" to mapOf(
            AppLanguage.ENGLISH to "Apply Interface Language",
            AppLanguage.THAI to "ยืนยันการใช้ภาษานี้",
            AppLanguage.HEBREW to "החל שפת ממשק זו",
            AppLanguage.JAPANESE to "新しいシステム言語を適用する",
            AppLanguage.KOREAN to "선택한 언어로 시스템 즉시 변경",
            AppLanguage.CHINESE to "应用当前界面语言",
            AppLanguage.RUSSIAN to "Применить новый язык",
            AppLanguage.ARABIC to "تطبيق لغة الواجهة هذه",
            AppLanguage.SPANISH to "Aplicar Nuevo Idioma"
        ),
        "active" to mapOf(
            AppLanguage.ENGLISH to "Active",
            AppLanguage.THAI to "เปิดใช้งานอยู่",
            AppLanguage.HEBREW to "פעיל כעת",
            AppLanguage.JAPANESE to "有効化中",
            AppLanguage.KOREAN to "정상 적용됨",
            AppLanguage.CHINESE to "当前活动",
            AppLanguage.RUSSIAN to "Активно",
            AppLanguage.ARABIC to "النشط حالياً",
            AppLanguage.SPANISH to "Activo Ahora"
        ),
        "solo_interpreter" to mapOf(
            AppLanguage.ENGLISH to "Solo Interpreter",
            AppLanguage.THAI to "โหมดล่ามเดี่ยวฟัง-พูด",
            AppLanguage.HEBREW to "מתורגמן קולי יחיד",
            AppLanguage.JAPANESE to "単独同時通訳アシスタント",
            AppLanguage.KOREAN to "일대일 자동 통역 전문가",
            AppLanguage.CHINESE to "单人快速同传翻译",
            AppLanguage.RUSSIAN to "Соло-интерпретатор речи",
            AppLanguage.ARABIC to "المترجم الفوري الفردي",
            AppLanguage.SPANISH to "Intérprete de Voz Solo"
        ),
        "duo_conversation" to mapOf(
            AppLanguage.ENGLISH to "Duo Conversation",
            AppLanguage.THAI to "คู่สนทนาแบ่งฝ่ายโต้ตอบ",
            AppLanguage.HEBREW to "שיחה דו-צדדית מתורגמת",
            AppLanguage.JAPANESE to "対面二者間スペース同時通訳",
            AppLanguage.KOREAN to "2인 분할 화면 양방향 대화",
            AppLanguage.CHINESE to "分屏式双人面对面会话",
            AppLanguage.RUSSIAN to "Двусторонний диалог с переводом",
            AppLanguage.ARABIC to "المحادثة الثنائية المشتركة",
            AppLanguage.SPANISH to "Conversación de Diálogo a Dúo"
        ),
        "chat_buddy_title" to mapOf(
            AppLanguage.ENGLISH to "Chat Buddy AI",
            AppLanguage.THAI to "คุยเล่นกับ AI บัดดี้",
            AppLanguage.HEBREW to "צ'אט באדי תבוני",
            AppLanguage.JAPANESE to "AI会話のチャットバディ",
            AppLanguage.KOREAN to "인공지능 회화 친구 챗버디",
            AppLanguage.CHINESE to "AI 语言陪练聊天伙伴",
            AppLanguage.RUSSIAN to "ИИ-Собеседник Чат",
            AppLanguage.ARABIC to "رفيق الدردشة الذكي AI",
            AppLanguage.SPANISH to "IA Compañero de Conversación"
        ),
        "chat_buddy_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Discuss topics, grammar queries, and converse naturally.",
            AppLanguage.THAI to "คุยได้ทุกเรื่องตามใจปรารถนา ถามหลักภาษาก็เข้าใจง่าย",
            AppLanguage.HEBREW to "דון בנושאים מעניינים, שלח שאלות דקדוק ושוחח בכיף.",
            AppLanguage.JAPANESE to "任意の英語学習や文法、世間話をAIと自然なやり取りでトレーニング。",
            AppLanguage.KOREAN to "어떤 대화 주제든 상호작용하며 문법 실교정과 즐거운 텍스트 학습을 제공합니다.",
            AppLanguage.CHINESE to "练习日常口语、解答语法疑惑、自然畅通地进行双向沟通。",
            AppLanguage.RUSSIAN to "Обсуждайте темы, пишите запросы о грамматике и общайтесь.",
            AppLanguage.ARABIC to "ناقش شتى مواضيع الحياة، القواعد اللغوية وتكلم بطلاقة طبيعية.",
            AppLanguage.SPANISH to "Habla de cualquier tema, aclara dudas gramaticales e instruye en el habla."
        ),
        "chat_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Ask Chat Buddy anything...",
            AppLanguage.THAI to "คุยอะไรกับบัดดี้ดีนะ พิมพ์ส่งข้อความด่วน...",
            AppLanguage.HEBREW to "שאל את צ'אט באדי הכל...",
            AppLanguage.JAPANESE to "気になる質問や挨拶を送ってみましょう...",
            AppLanguage.KOREAN to "선생님처럼 친절한 챗버디에게 말을 걸어보세요...",
            AppLanguage.CHINESE to "向您的 AI 专属助理提问任何问题吧...",
            AppLanguage.RUSSIAN to "Спросите ИИ-собеседника о чем угодно...",
            AppLanguage.ARABIC to "اكتب رسالة أو استفسر عن أي شيء يخطر ببالك...",
            AppLanguage.SPANISH to "Pregúntale al Compañero de Chat lo que desees..."
        ),
        "lens_title" to mapOf(
            AppLanguage.ENGLISH to "Neural Lens OCR",
            AppLanguage.THAI to "สแกนกล้องอ่านข้อความอัจฉริยะ",
            AppLanguage.HEBREW to "עדשה נוירולוגית לזיהוי טקסט",
            AppLanguage.JAPANESE to "ニューラル認知レンズ OCR",
            AppLanguage.KOREAN to "뉴럴 카메라 판독 렌즈 OCR",
            AppLanguage.CHINESE to "神经网络视觉镜头 OCR 翻译",
            AppLanguage.RUSSIAN to "Нейрообъектив OCR сканер",
            AppLanguage.ARABIC to "عدسة قراءة النصوص العصبية OCR",
            AppLanguage.SPANISH to "Lente Neuronal Foto OCR"
        ),
        "lens_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Visual scanning and real-time translation processing using camera streams",
            AppLanguage.THAI to "แกะตัวอักษรจากเลนส์กล้องภาพภาพถ่ายแล้วถอดความหมายให้อัตโนมัติ",
            AppLanguage.HEBREW to "סריקה חזותית ועיבוד תרגום ישירות מזרם המצלמה ובזמן אמת",
            AppLanguage.JAPANESE to "カメラをかざすだけで画像を高速で認識し、リアルタイムに翻訳。",
            AppLanguage.KOREAN to "실시간 카메라 데이터 스트림을 활용하여 이미지 속 외국 문자를 자동으로 분석 정밀 번역.",
            AppLanguage.CHINESE to "运用深度计算机视觉，通过相机实时捕捉并快速渲染翻译文本。",
            AppLanguage.RUSSIAN to "Визуальное сканирование печатного текста и динамический моментальный перевод с камеры",
            AppLanguage.ARABIC to "مسح بصري متقدم ومعالجة النصوص المترجمة فورياً بواسطة كاميرا الهاتف",
            AppLanguage.SPANISH to "Escaneo de imágenes capturadas y traducción en tiempo real con su cámara"
        ),
        "lens_simulate_scan" to mapOf(
            AppLanguage.ENGLISH to "Simulate Neural Scanning",
            AppLanguage.THAI to "เริ่มจำลองสแกนภาพอิมเมจ",
            AppLanguage.HEBREW to "הפעל סימולציית סריקה",
            AppLanguage.JAPANESE to "高度画像スキャンシミュレート",
            AppLanguage.KOREAN to "카메라 렌즈 인지 번역 테스트",
            AppLanguage.CHINESE to "运行神经网络镜头扫描测试",
            AppLanguage.RUSSIAN to "Запустить симуляцию нейросканирования",
            AppLanguage.ARABIC to "بدء محاكاة عملية القراءة والترجمة",
            AppLanguage.SPANISH to "Simular Escaneo y Lectura Óptica"
        ),
        "scanned_image_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Interactive Scanned Image Feed Placeholder",
            AppLanguage.THAI to "หน้าจอสตรีมกล้องจำลองอินเตอร์แอคทีฟ",
            AppLanguage.HEBREW to "ממלא מקום עבור תמונת סריקה אינטראקטיבית",
            AppLanguage.JAPANESE to "インタラクティブ撮影フィードスペース",
            AppLanguage.KOREAN to "상호작용 연동 스캐너 촬영 라이브 뷰",
            AppLanguage.CHINESE to "高对比度实景增强多视角拍照框",
            AppLanguage.RUSSIAN to "Интерактивная панель виртуального видоискателя",
            AppLanguage.ARABIC to "منطقة معاينة عدسة الكاميرا النشطة",
            AppLanguage.SPANISH to "Feed de Imagen Interactivo del Visor de Cámara"
        ),
        "ocr_scanned_details" to mapOf(
            AppLanguage.ENGLISH to "OCR Scanned Output Extracted",
            AppLanguage.THAI to "ดึงคำอักษรที่สแกนเสร็จแล้ว",
            AppLanguage.HEBREW to "טקסט מחולץ בתום סריקת ה-OCR",
            AppLanguage.JAPANESE to "画像から文字起こしされた成果",
            AppLanguage.KOREAN to "이미지에서 정밀 분석된 원문 및 처리결과",
            AppLanguage.CHINESE to "图片解析完成！已捕获文本",
            AppLanguage.RUSSIAN to "Распознанные символы с фотографии",
            AppLanguage.ARABIC to "نصوص المسح الضوئي المستخرجة بنجاح",
            AppLanguage.SPANISH to "Resultado del Escaneo Óptico OCR"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        val map = strings[key] ?: return key
        return map[language] ?: map[AppLanguage.ENGLISH] ?: key
    }
}
