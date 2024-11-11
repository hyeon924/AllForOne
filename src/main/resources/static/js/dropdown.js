// 각 드롭다운 요소를 선택
const dropdowns = document.querySelectorAll('.drop-option');

// 드롭다운 토글 함수 정의
function toggleDropdown(dropdown) {
  const value = dropdown.querySelector('.drop-option_value'); // 드롭다운 값 요소 선택
  const toggle = dropdown.querySelector('.drop-option-toggle'); // 드롭다운 토글 요소 선택
  const menu = dropdown.querySelector('.drop-option-menu'); // 드롭다운 메뉴 요소 선택

  // 다른 드롭다운에서 열린 drop-option-menu 닫기
  dropdowns.forEach(d => {
    if (d !== dropdown) {
      const otherMenu = d.querySelector('.drop-option-menu'); // 다른 드롭다운의 메뉴 요소 선택
      otherMenu.classList.remove('open'); // 다른 드롭다운의 메뉴를 닫음
      otherMenu.style.height = '0'; // 닫힌 드롭다운 메뉴의 높이를 0으로 설정하여 애니메이션 효과 적용

      // 다른 드롭다운을 클릭했을 때 이미지 초기화
      const otherDropdownIcon = d.querySelector('img');
      otherDropdownIcon.src = 'img/svg/range_down.svg'; // 경로 수정
    }
  });

  // drop-option-menu 열기/닫기
  const isOpen = menu.classList.toggle('open'); // drop-option-menu 요소에 'open' 클래스를 토글하여 열림/닫힘 상태를 확인
  menu.style.height = isOpen ? `${menu.scrollHeight}px` : '0'; // 열린 drop-option-menu의 높이를 실제 내용의 높이로 설정하거나 닫힌 상태일 때 0으로 설정하여 애니메이션 효과 적용

  // 드롭다운 아이콘 변경
  const dropdownIcon = dropdown.querySelector('img'); // 이미지 요소 선택
  dropdownIcon.src = isOpen ? 'img/svg/range_up.svg' : 'img/svg/range_down.svg'; // 아이콘 변경
}

// 각 드롭다운 요소에 이벤트 리스너 추가
dropdowns.forEach(dropdown => {
  const value = dropdown.querySelector('.drop-option_value'); // 드롭다운 값 요소 선택
  const toggle = dropdown.querySelector('.drop-option-toggle'); // 드롭다운 토글 요소 선택
  const menu = dropdown.querySelector('.drop-option-menu'); // 드롭다운 메뉴 요소 선택

  // 드롭다운 값 요소를 클릭할 때 토글 함수 호출
  value.addEventListener('click', () => {
    toggleDropdown(dropdown);
  });

  // 드롭다운 메뉴의 항목을 클릭할 때 선택된 아이템을 표시
  menu.addEventListener('click', event => {
    if (event.target.classList.contains('drop-option-item') || event.target.tagName === 'SPAN') {
      const selectedItem = event.target.closest('.drop-option-item'); // 선택된 드롭다운 아이템 요소 선택
      toggle.textContent = selectedItem.textContent; // 드롭다운 토글 요소의 텍스트를 선택된 드롭다운 아이템의 텍스트로 설정
      toggleDropdown(dropdown); // 드롭다운을 열고 닫는 토글 함수 호출
    }
  });
});

// 다른 영역 클릭 시 드롭다운 메뉴 닫기
document.addEventListener('click', event => {
  dropdowns.forEach(dropdown => {
    const menu = dropdown.querySelector('.drop-option-menu'); // 드롭다운 메뉴 요소 선택

    // 이벤트 타깃이 드롭다운 영역과 메뉴 영역에 포함되지 않은 경우에만 drop-option-menu를 닫음
    if (!dropdown.contains(event.target) && !menu.contains(event.target)) {
      menu.classList.remove('open'); // drop-option-menu 닫기
      menu.style.height = '0'; // drop-option-menu의 높이를 0으로 설정하여 애니메이션 효과 적용

      // drop-option-menu의 영역이 사라질 때 이미지 초기화
      const dropdownIcon = dropdown.querySelector('img');
      dropdownIcon.src = 'img/svg/range_down.svg'; // 경로 수정
    }
  });
});
