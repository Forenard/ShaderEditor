# CLAUDE.md

このファイルは、このリポジトリで作業を行う際にClaude Code（claude.ai/code）に対するガイダンスを提供します。

## 開発コマンド

### ビルドコマンド
- `./gradlew assembleDebug` - デバッグAPKをビルド
- `./gradlew assembleRelease` - リリースAPKをビルド（最初にlintを実行）
- `./gradlew bundleRelease` - リリースバンドルを作成（最初にlintを実行）
- `./gradlew clean` - ビルドアーティファクトをクリーン
- `./gradlew installDebug` - 実機にデバッグAPKをインストール

### 品質保証
- `./gradlew lintDebug` - Android lintチェックを実行
- `infer -- ./gradlew assembleDebug` - Facebook Infer静的解析を実行（事前にクリーンが必要）

### デバイス操作（Makefileを使用）
- `make debug` - デバッグAPKをビルド
- `make install` - 接続されたデバイスにデバッグAPKをインストール
- `make start` - 接続されたデバイスでアプリを起動
- `make all` - ビルド、インストール、起動（デフォルトターゲット）
- `make uninstall` - デバイスからデバッグバージョンを削除

### 実機テスト
- `adb devices` - 接続されたデバイス一覧を表示
- `adb logcat` - アプリケーションログを表示
- `adb logcat -c` - ログキャットバッファをクリア
- `adb logcat -s MainActivity` - MainActivityのログを表示
- `adb logcat -c && adb logcat | grep "ShaderEditor"` - アプリ固有のログを表示
- `adb shell dumpsys meminfo de.markusfisch.android.shadereditor` - アプリのメモリ使用状況を表示
- `adb shell am force-stop de.markusfisch.android.shadereditor` - アプリを強制終了
- `adb shell am start -n de.markusfisch.android.shadernerdeditor.debug/de.markusfisch.android.shadernerdeditor.activity.SplashActivity` - アプリを起動

### ユーティリティコマンド
- `make meminfo` - 実行中アプリのメモリ使用量を表示
- `make glxinfo` - アプリのGPU/グラフィックス情報を表示
- `make images` - SVGベースのアプリアイコンを更新
- `make avocado` - ベクタードローアブルファイルを最適化

## アーキテクチャ概要

これは、ユーザーがシェーダーを作成、編集し、ライブ壁紙として使用できるAndroid GLSLシェーダーエディターアプリです。

### 主要コンポーネント

**アプリケーション層**
- `ShaderNerdEditorApp.java` - メインアプリケーションクラス、グローバル状態を管理（設定、データベース、編集履歴）
- `MainActivity.java` - ドロワーナビゲーションとシェーダー管理を持つプライマリアクティビティ

**OpenGLレンダリング**
- `ShaderRenderer.java` - コアOpenGL ESレンダラー、シェーダーコンパイルとレンダリングを処理
- `Program.java` - OpenGLプログラム管理（頂点/フラグメントシェーダー）
- `BackBufferParameters.java` - バックバッファテクスチャ用のフレームバッファを管理
- `TextureParameters.java` - テクスチャ設定とパラメータ

**エディターシステム**
- `EditorFragment.java` - メインシェーダー編集インターフェース
- `ShaderEditor.java` - シェーダーコード編集用のカスタムウィジェット
- `LineNumberEditText.java` - 行番号付きテキストエディター
- `UndoRedo.java` - 編集履歴管理

**ハードウェア統合**
- `hardware/`パッケージ内のハードウェアセンサーリスナー（加速度計、ジャイロスコープ、カメラなど）
- `BatteryLevelReceiver.java` - バッテリーレベル監視

**データ管理**
- `Database.java` - シェーダー保存用SQLiteデータベース
- `Preferences.java` - アプリケーション設定管理

### パッケージ構造
- `de.markusfisch.android.shadernerdeditor` - ルートパッケージ
- `.activity` - UIアクティビティ
- `.opengl` - OpenGLレンダリングコンポーネント
- `.fragment` - UIフラグメント
- `.hardware` - ハードウェアセンサー統合
- `.database` - データ永続化
- `.widget` - カスタムUIウィジェット

### 主要機能
- OpenGL ES 3.0サポート（2.0への下位互換性あり）
- `ShaderWallpaperService.java`によるライブ壁紙統合
- シェーダーへのハードウェアセンサー公開
- テクスチャとキューブマップサポート
- シンタックスハイライトとエラー報告
- インポート/エクスポート機能

### 開発ノート
- Kotlin DSLでGradleビルド設定を使用
- 最小SDK: 21（Android 5.0）
- ターゲット/コンパイルSDK: 34（Android 14）
- Java 17互換
- ビューアクセスにViewBindingを使用
- AndroidXライブラリによるMaterial Designコンポーネント