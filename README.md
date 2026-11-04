# Smart Time Card
## 1.概要
Smart Time Cardは、勤務時間を記録・管理するための勤怠管理アプリです。
出勤・退勤打刻、勤務一覧表示、勤務情報修正、勤務集計、通知設定を行うことが出来ます。
また、Render上に公開しているため、PC・スマートフォンのブラウザから利用できます。

## 2.公開URL
https://smart-time-card.onrender.com

## 3.開発目的
JavaとSpringBootを使用したWebアプリケーション開発の学習を目的として作成しました。
ローカル環境だけでなく、
- PostgreSQL
- Docker
- Render
- を利用してクラウド公開まで実施しました。

## 4.主な機能
### 4-1.打刻機能
- 出勤登録
- 退勤登録
- 在宅／出社選択

### 4-2.一覧機能
- 勤務履歴表示
- 勤務内容編集
- 勤務履歴削除

### 4-3.集計機能
- 月別勤務集計
- 勤務時間計算

### 4-4.設定機能
- デフォルト休憩時間設定
- 通知時刻設定

## 5.使用技術
### 5-1.Backend
- Java 21
- Sprint Boot
- Spring Data JPA
- Thymeleaf

### 5-2.Database
- PostgreSQL

### 5-3.Frontend
- HTML
- TailwindCSS
- JavaScript

### 5-4.Infrastructure
- Docker
- Render
- GitHub

## 6.データベース
### 6-1.TimeCard
| 名前 | 内容 |
| --- | --- |
| id | ID |
| workDate | 勤務日 |
| startTime | 出勤時間 |
| endTime | 退勤時間 |
| workType | 勤務形態 |
| breakTime | 休憩時間 |

### 6-2.Settings
| 名前 | 内容 |
| --- | --- |
| defaultBreakMinutes | デフォルト休憩時間 |
| startNoticeEnabled | 出勤通知チェック |
| endNoticeEnabled | 退勤通知チェック |
| startNoticeTime | 出勤通知 |
| endNoticeTime | 退勤通知 |
| startScheduledTime | 勤務開始予定 |
| endScheduledTime | 勤務終了予定 |

## 7.工夫した点
- RenderはJavaアプリのデプロイにDockerを使うと良いとのことでDockerを使いました。
- 時刻を打刻するときに ZoneId.of("Asia/Tokyo") を追記

## 8.画面写真
- [打刻画面/index](images/IMG_01_index.PNG)
- [一覧画面/display](images/IMG_02_display.PNG)
- [集計画面/summary](images/IMG_03_summary.PNG)
- [設定画面/settings](images/IMG_04_settings.PNG)

## 9.今後の改善予定
- アイコン／favicon追加
- CSV出力
- 勤務実績グラフ表示

など