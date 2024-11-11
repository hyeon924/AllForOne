 // 별점 요소들 선택
    const stars = document.querySelectorAll('.star-rating i');
    const ratingValue = document.getElementById('ratingValue');
    
    // 각 별에 대한 클릭 이벤트 처리
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = this.getAttribute('data-value'); // 클릭한 별의 값
            updateRating(value);
        });
    });
    
    // 별점과 숫자 업데이트 함수
    function updateRating(value) {
        // 숫자 업데이트
        ratingValue.innerText = value;

        // 별점 업데이트
        stars.forEach(star => {
            star.classList.remove('bi-star-fill', 'active');
            star.classList.add('bi-star'); // 모든 별을 빈 별로 설정
            if (star.getAttribute('data-value') <= value) {
                star.classList.remove('bi-star');
                star.classList.add('bi-star-fill', 'active'); // 선택한 별 이하를 채워진 별로 설정
            }
        });
    }