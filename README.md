# batchSaveSPXX
SPXX导入Redis

SPXX表，主键: BARCODE

| key            | type | 说明        |
| -------------- | ---- | ----------- |
| SPXX           | set  | barcode集合 |
| SPXX:{BARCODE} | hash | 商品明细    |

SPKCZT，主键: FDBH, BARCODE

| key                              | type | 说明                  |
| -------------------------------- | ---- | --------------------- |
| SPKCZT:FD:{FD}                   | set  | {FD}分店下barcode集合 |
| SPKCZT:BARCODE:{BARCODE}         | set  | {BARCODE}条码下fd集合 |
| SPKCZT:FD:{FD}:BARCODE:{BARCODE} | hash | 商品库存状态明细      |
