### 状態

[![Java CI with Gradle](https://github.com/gnya/SleepInSheep/actions/workflows/build.yml/badge.svg)](https://github.com/gnya/SleepInSheep/actions/workflows/build.yml)

### 未完了

- [ ] 羊から出るパーティクルが微妙なので修正する
- [ ] forge以外にも対応させる
- [ ] テスト(可能なら)、CI/CDの自動化
    - [x] devブランチを作る
    - [x] TODOをREADMEから移動させる
        - [x] 過去のTODOも見れるようにする
    - [x] GameTestを利用したテストの追加
        - [ ] Loaderがtests以下のクラスを自動で取ってくるようにする
- [ ] updates.jsonに対応する？

### 完了

### 1.0.0-beta.0

- [x] 村人が羊の上で寝るようにする
    - [x] BedSheepSensorを追加、最寄りの羊を記憶するようにする
    - [x] 羊の上で村人が浮いてしまうのを修正する
    - [x] 羊の上で村人が立ったままになるのを修正する
        - [x] またShiftで…の表示が出るので修正
        - [x] 一瞬体力ゲージが表示されるのを修正
    - [x] 羊の毛を刈り取れなくなってるのを修正する
- [x] Happyな羊を染料で染められない問題の修正
- [x] 羊の綿毛に専用のパーティクルを追加する
- [x] 村人がHappyな羊を見るようにする
- [x] 羊の上で寝た際に体力を30%ぐらい回復させる
- [x] 雑草などを食べる場合も多く食べるようにする
- [x] 50%の確率でうつ伏せで寝るようにする
- [x] 寝ているときのプレイヤーの位置を頭に合わせる
- [x] 寝ているEntityの頭からZzzのパーティクルを出す
- [x] ネザーで寝ようとすると爆発するようにする
- [x] 村人が羊を追いかけるようにする
- [x] 村人が寝るとクラッシュする問題の修正

#### Sheep側の実装

- [x] Sheepに状態Happyを追加する
- [x] HappyなSheepがスポーンするようにする
- [x] Happyな羊の上に乗る際の位置に補正をいれる
- [x] Happyな羊の場合に当たり判定を大きくする
- [x] 子どもやHappyでない羊の上では眠れないようにする
- [x] Happyな子どもの羊が生まれるようにする
- [x] Happyな羊の鳴き声を低くする
- [x] Happyな羊の体力を32にする
- [x] クリックするときの判定が小さい気がする
- [x] Happyな羊がドロップする羊毛を増やす
- [x] Happyな羊が一度に食べる草ブロックの数を増やす
- [x] Happyな羊が時々羊毛を落とすようにする
- [x] Happyな羊の周りに綿毛が舞うようにする

#### Player側の実装

- [x] LivingEntityに状態SleepInSheepを追加する
- [x] LivingEntityのstartSleeping/stopSleepingを羊に対応させる
- [x] ServerPlayerのstartSleeping/stopSleepingを羊に対応させる
- [x] PlayerのstartSleepInBed/stopSleepInBedを羊に対応させる
- [x] ServerPlayerのstartSleepInBed/stopSleepInBedを羊に対応させる
- [x] プレイヤーが羊の上で寝ると夜を明かせるようにする
- [x] 近くに敵がいる場合は眠れないようにする
- [x] 誰かが既に寝ている場合は眠れないようにする
- [x] 眠れない場合にベッドと同様のメッセージを表示する
- [x] 眠っているとき羊の体力を表示されないようにする
- [x] Shiftで降りる…のメッセージを表示させない
- [x] どこで寝ているか見えないので寝ている際のカメラの位置を調整する
- [x] 寝ている際のプレイヤーの位置がおかしい
- [x] 寝ている際のプレイヤーの当たり判定がおかしい
- [x] オフセットの値を乗る側のEntityが指定する