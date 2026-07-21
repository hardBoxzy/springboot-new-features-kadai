let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);//変数maxDateに3か月後の日付を代入する

flatpickr('#fromCheckinDateToCheckoutDate', {//対象となるセレクタ：#fromCheckinDateToCheckoutDate（id="fromCheckinDateToCheckoutDate"が設定されたHTML要素）
 mode: "range",//カレンダーの範囲選択モードを有効にする（チェックイン日とチェックアウト日を同時に指定できるようになる）
 locale: 'ja',//カレンダーの言語を日本語にする
 minDate: 'today',//カレンダーで選択できる最小の日付を当日にする
 maxDate: maxDate //カレンダーで選択できる最大の日付を3か月後にする
});