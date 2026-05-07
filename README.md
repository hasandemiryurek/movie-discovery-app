<img width="350" height="250" alt="icon" src="https://github.com/user-attachments/assets/44e0245c-624a-4b55-b559-b89ca5c8ddc6" />



# MovieDiscover

**MovieDiscover**, sinema dünyasının kapılarını aralayan modern, hızlı ve kullanıcı dostu bir Android film keşif uygulamasıdır. TMDB API altyapısını kullanarak en güncel filmleri listeler ve detaylı analizler sunar.

## Uygulama Görselleri
| Ana Sayfa | Arama | Film Detay |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/9c2c78ed-b097-45e6-af70-190c20039a69"  width="250"> | <img src="https://github.com/user-attachments/assets/5513b7a6-f54a-4e95-8102-beb94fa81ad1" width="250"> | <img src="https://github.com/user-attachments/assets/4a8fcc62-fec8-44f1-a5db-ad6fb4b9a115" width="250"> |





---

## Teknik Mimari ve Tasarım Kararları

Uygulama, sürdürülebilirlik ve test edilebilirlik prensipleri doğrultusunda **Clean Architecture** prensiplerine uygun olarak **MVVM (Model-View-ViewModel)** mimarisiyle geliştirilmiştir.

### Kullanılan Teknolojiler
* **Jetpack Compose:** Deklaratif bir UI yapısı kullanılarak modern bir arayüz geliştirildi.
* **Retrofit & OkHttp:** API istekleri ve ağ yönetimi asenkron olarak yapılandırıldı.
* **Coil:** Film afişlerinin performanslı ve asenkron yüklenmesi için tercih edildi.
* **Hilt (Dependency Injection):** Bağımlılıkların yönetimi, modülerlik ve kodun test edilebilirliğini artırmak için kullanıldı.
* **Kotlin Coroutines & Flow:** Veri akışları ve arka plan işlemleri reaktif bir şekilde yönetildi.

---

## Yerelleştirme ve Dil Desteği 

Uygulama, modern Android standartlarına uygun olarak **Türkçe ve İngilizce** dillerini desteklemektedir.
* **Uygulama İçi Dil Değişimi:** Kullanıcıya telefon ayarlarını değiştirmeden uygulama üzerinden dil değiştirme imkanı sunan `AppCompatDelegate` yapısı entegre edilmiştir.
* **Android 13+ Uyumu:** `locales_config.xml` yapılandırmasıyla yeni nesil Android cihazlarla tam uyumlu çalışmaktadır.

---

## API Kurulumu ve Güvenlik

Projede güvenlik gereği API anahtarları kaynak kodda tutulmamaktadır. Projeyi çalıştırmak için:

1. `local.properties` dosyasını açın.
2. Aşağıdaki satırı ekleyin:
   ```properties
   TMDB_API_KEY=YOUR_API_KEY_HERE
