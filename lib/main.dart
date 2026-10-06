import 'package:flutter/material.dart';

void main() {
  runApp(const QuranLearningApp());
}

class QuranLearningApp extends StatelessWidget {
  const QuranLearningApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'مدرسة القرآن الكريم',
      theme: ThemeData(
        primarySwatch: Colors.teal,
        scaffoldBackgroundColor: const Color(0xFFF4F7F5),
        fontFamily: 'Roboto',
      ),
      home: const MainTabScreen(),
    );
  }
}

// شاشة التنقل الرئيسية (تحتوي على التبويبات بالأسفل)
class MainTabScreen extends StatefulWidget {
  const MainTabScreen({super.key});

  @override
  State<MainTabScreen> createState() => _MainTabScreenState();
}

class _MainTabScreenStateState extends State<MainTabScreen> {
  int _currentIndex = 0;
  final List<Widget> _screens = const [
    HomeScreen(),
    TajweedScreen(),
    AboutScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        body: _screens[_currentIndex],
        bottomNavigationBar: BottomNavigationBar(
          currentIndex: _currentIndex,
          selectedItemColor: Colors.teal,
          unselectedItemColor: Colors.grey,
          onTap: (index) {
            setState(() {
              _currentIndex = index;
            });
          },
          items: const [
            BottomNavigationBarItem(icon: Icon(Icons.menu_book), label: 'السور التعليمية'),
            BottomNavigationBarItem(icon: Icon(Icons.gavel), label: 'أحكام التجويد'),
            BottomNavigationBarItem(icon: Icon(Icons.info), label: 'عن التطبيق'),
          ],
        ),
      ),
    );
  }
}

// 1. الشاشة الرئيسية وقائمة السور مع ميزة البحث
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final List<Map<String, dynamic>> _allSurahs = const [
    {
      'id': 1,
      'name': 'الفاتحة',
      'type': 'مكية',
      'versesCount': '7 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ',
        'الرَّحْمَٰنِ الرَّحِيمِ',
        'مَالِكِ يَوْمِ الدِّينِ',
        'إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ',
        'اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ',
        'صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ'
      ]
    },
    {
      'id': 2,
      'name': 'الإخلاص',
      'type': 'مكية',
      'versesCount': '4 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'قُلْ هُوَ اللَّهُ أَحَدٌ',
        'اللَّهُ الصَّمَدُ',
        'لَمْ يَلِدْ وَلَمْ يُولَدْ',
        'وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ'
      ]
    },
    {
      'id': 3,
      'name': 'الفلق',
      'type': 'مكية',
      'versesCount': '5 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'قُل_ أَعُوذُ بِرَبِّ الْفَلَقِ',
        'مِن شَرِّ مَا خَلَقَ',
        'وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ',
        'وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ',
        'وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ'
      ]
    },
    {
      'id': 4,
      'name': 'الناس',
      'type': 'مكية',
      'versesCount': '6 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'قُلْ أَعُوذُ بِرَبِّ النَّاسِ',
        'مَلِكِ النَّاسِ',
        'إِلَٰهِ النَّاسِ',
        'مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ',
        'الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ',
        'مِنَ الْجِنَّةِ وَالنَّاسِ'
      ]
    },
    {
      'id': 5,
      'name': 'الكوثر',
      'type': 'مكية',
      'versesCount': '3 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'إِنَّا أَعْطَيْنَاكَ الْكَوْتَثَرَ',
        'فَصَلِّ لِرَبِّكَ وَانْحَرْ',
        'إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ'
      ]
    },
    {
      'id': 6,
      'name': 'النصر',
      'type': 'مدنية',
      'versesCount': '3 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ',
        'وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا',
        'فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا'
      ]
    },
    {
      'id': 7,
      'name': 'الكافرون',
      'type': 'مكية',
      'versesCount': '6 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'قُلْ يَا أَيُّهَا الْكَافِرُونَ',
        'لَا أَعْبُدُ مَا تَعْبُدُونَ',
        'وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ',
        'وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ',
        'وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ',
        'لَكُمْ دِينُكُمْ وَلِيَ دِينِ'
      ]
    }
  ];

  List<Map<String, dynamic>> _filteredSurahs = [];
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _filteredSurahs = _allSurahs;
  }

  void _searchSurah(String query) {
    setState(() {
      _searchQuery = query;
      if (query.isEmpty) {
        _filteredSurahs = _allSurahs;
      } else {
        _filteredSurahs = _allSurahs
            .where((surah) => surah['name'].contains(query))
            .toList();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('مدرسة القرآن التعليمية', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
        backgroundColor: Colors.teal,
        centerTitle: true,
        elevation: 4,
      ),
      body: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          children: [
            // بطاقة الحديث الشريف
            Card(
              color: Colors.amber.shade100,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
              child: const Padding(
                padding: EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    Icon(Icons.auto_stories, color: Colors.teal, size: 32),
                    SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        'قال ﷺ: "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ"',
                        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.teal),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 15),
            // حقل البحث عن السور
            TextField(
              onChanged: _searchSurah,
              decoration: InputDecoration(
                hintText: 'ابحث عن سورة...',
                prefixIcon: const Icon(Icons.search, color: Colors.teal),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
            const SizedBox(height: 15),
            // قائمة السور المفلترة
            Expanded(
              child: _filteredSurahs.isEmpty
                  ? const Center(child: Text('لم يتم العثور على السورة'))
                  : ListView.builder(
                      itemCount: _filteredSurahs.length,
                      itemBuilder: (context, index) {
                        final surah = _filteredSurahs[index];
                        return Card(
                          elevation: 3,
                          margin: const EdgeInsets.symmetric(vertical: 6),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          child: ListTile(
                            leading: CircleAvatar(
                              backgroundColor: Colors.teal.shade400,
                              child: Text('${surah['id']}', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                            ),
                            title: Text(surah['name'], style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                            subtitle: Text('${surah['type']} • ${surah['versesCount']}'),
                            trailing: const Icon(Icons.arrow_forward_ios, color: Colors.teal, size: 18),
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => SurahStudyScreen(
                                    surahName: surah['name'],
                                    verses: List<String>.from(surah['verses']),
                                  ),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }
}

// 2. شاشة الحفظ والتعليم المتطورة مع عداد التكرار وحفظ الموضع
class SurahStudyScreen extends StatefulWidget {
  final String surahName;
  final List<String> verses;

  const SurahStudyScreen({super.key, required this.surahName, required this.verses});

  @override
  State<SurahStudyScreen> createState() => _SurahStudyScreenState();
}

class _SurahStudyScreenState extends State<SurahStudyScreen> {
  Map<int, int> repeatCounters = {}; // لتتبع تكرار كل آية للحفظ
  int lastReadVerse = -1; // لحفظ آخر آية وقف عندها المستخدم

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: Text('سورة ${widget.surahName}', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
