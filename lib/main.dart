import 'package:flutter/material.dart';

void main() {
  runApp(const QuranAcademyApp());
}

class QuranAcademyApp extends StatelessWidget {
  const QuranAcademyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'تعليم القرآن الكريم',
      theme: ThemeData(
        primarySwatch: Colors.teal,
        scaffoldBackgroundColor: const Color(0xFFF9F9F6),
        fontFamily: 'Roboto', // يمكنك تخصيص خط عربي لاحقاً
      ),
      home: const HomeScreen(),
    );
  }
}

// 1. الشاشة الرئيسية: قائمة السور التعليمية
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  final List<Map<String, dynamic>> surahs = const [
    {
      'name': 'الفاتحة',
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
      'name': 'الإخلاص',
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
      'name': 'الفلق',
      'versesCount': '5 آيات',
      'verses': [
        'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ',
        'قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ',
        'مِن شَرِّ مَا خَلَقَ',
        'وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ',
        'وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ',
        'Wَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ'
      ]
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('مدرسة القرآن التعليمية', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
          backgroundColor: Colors.teal[800],
          centerTitle: true,
        ),
        body: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // بطاقة ترحيبية أو نصيحة اليوم
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.amber[100],
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.amber, width: 1),
                ),
                child: Row(
                  children: [
                    Icon(Icons.star, color: Colors.amber[800], size: 30),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        'قال رسول الله ﷺ: "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ"',
                        style: TextStyle(fontSize: 16, color: Colors.teal[900], fontWeight: FontWeight.bold),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              Text(
                'اختر سورة للبدء في التعلم:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.teal[900]),
              ),
              const SizedBox(height: 10),
              // قائمة السور
              Expanded(
                child: ListView.builder(
                  itemCount: surahs.length,
                  itemBuilder: (context, index) {
                    return Card(
                      elevation: 2,
                      margin: const EdgeInsets.symmetric(vertical: 8),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: Colors.teal,
                          child: Text('${index + 1}', style: const TextStyle(color: Colors.white)),
                        ),
                        title: Text(surahs[index]['name'], style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        subtitle: Text(surahs[index]['versesCount']),
                        trailing: const Icon(Icons.arrow_forward_ios, color: Colors.teal),
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => SurahDetailScreen(
                                surahName: surahs[index]['name'],
                                verses: surahs[index]['verses'],
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
        // زر للانتقال لقواعد التجويد
        floatingActionButton: FloatingActionButton.extended(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const TajweedRulesScreen()),
            );
          },
          label: const Text('قواعد التجويد', style: TextStyle(color: Colors.white)),
          icon: const Icon(Icons.gavel, color: Colors.white),
          backgroundColor: Colors.amber[700],
        ),
      ),
    );
  }
}

// 2. شاشة عرض السورة والتعليم الآية تلو الآية
class SurahDetailScreen extends StatelessWidget {
  final String surahName;
  final List<String> verses;

  const SurahDetailScreen({super.key, brewery, required this.surahName, required this.verses});

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: Text('سورة $surahName', style: const TextStyle(color: Colors.white)),
          backgroundColor: Colors.teal[800],
        ),
        body: ListView.builder(
          padding: const EdgeInsets.all(16),
          itemCount: verses.length,
          itemBuilder: (context, index) {
            return Card(
              color: index == 0 && surahName != 'الفاتحة' ? Colors.teal[50] : Colors.white, // تمييز البسملة
              margin: const EdgeInsets.symmetric(vertical: 6),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      verses[index],
                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, height: 1.8),
                      textAlign: TextAlign.center,
                    ),
                    const Divider(),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text('آية رقم [ ${index + 1} ]', style: TextStyle(color: Colors.grey[600], fontSize: 12)),
                        TextButton.icon(
                          onPressed: () {
                            // هنا يمكن إضافة تشغيل الصوت لاحقاً
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('سيتم إضافة النطق الصوتي للآية قريباً إن شاء الله!')),
                            );
                          },
                          icon: const Icon(Icons.volume_up, size: 18),
                          label: const Text('استمع للنطق'),
                        )
                      ],
                    )
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

// 3. شاشة دروس التجويد المبسطة
class TajweedRulesScreen extends StatelessWidget {
  const TajweedRulesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('تعلم أحكام التجويد', style: TextStyle(color: Colors.white)),
          backgroundColor: Colors.amber[700],
        ),
        body: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildRuleCard('الإظهار', 'نطق النون الساكنة أو التنوين بوضوح من غير غنة إذا جاء بعدها أحد حروف الحلق: أ، هـ، ع، ح، غ، خ.', 'مثال: مَنْ عَمِلَ، جَنَّاتٍ أَلْفَافاً'),
            _buildRuleCard('الإدغام', 'دمج النون الساكنة أو التنوين بالحرف الذي بعدها ليفصيرا حرفاً واحداً مشدداً، وحروفه مجموعة في كلمة (يرملون).', 'مثال: مَن يَقُولُ، مِن رَّبِّهِمْ'),
            _buildRuleCard('القلقلة', 'اضطراب الصوت عند النطق بالحرف الساكن حتى يسمع له نبرة قوية، وحروفها مجموعة في (قطب جد).', 'مثال: الْفَلَقِ، أَحَدٌ'),
          ],
        ),
      ),
    );
  }

  Widget _buildRuleCard(String title, String explanation, String example) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      elevation: 3,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.teal)),
            const SizedBox(height: 8),
            Text(explanation, style: const TextStyle(fontSize: 15, height: 1.4)),
            const SizedBox(height: 10),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(color: Colors.grey[100], borderRadius: BorderRadius.circular(6)),
              
