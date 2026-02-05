document.addEventListener('DOMContentLoaded', function() {
    // インジケーター要素を作成
    const indicator = document.createElement('div');
    indicator.id = 'modeIndicator';
    indicator.className = 'mode-badge';
    document.body.appendChild(indicator);

    function updateMode() {
        const width = window.innerWidth;
        let modeText = '';

        // Bootstrapのブレークポイントに合わせる
        if (width < 576) {
            modeText = '📱 Mobile (Portrait)';
        } else if (width < 768) {
            modeText = '📱 Mobile (Landscape)';
        } else if (width < 992) {
            modeText = '💻 Tablet / Small PC';
        } else {
            modeText = '🖥️ PC / Large Screen';
        }

        indicator.textContent = modeText;
    }

    // 初回実行
    updateMode();

    // リサイズ時に実行
    window.addEventListener('resize', updateMode);
});