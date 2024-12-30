document.addEventListener('DOMContentLoaded', function () {
    const inquiryList = document.getElementById('inquiry-list');
    const emptyMessage = document.getElementById('empty-message');

    fetch('/inquires/user')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch inquiries');
            }
            return response.json();
        })
        .then(inquiries => {
            if (inquiries.length === 0) {
                emptyMessage.style.display = 'block';
                return;
            }

            inquiries.forEach(inquiry => {
                const box = document.createElement('div');
                box.className = 'inquiry-box';

                // 제목
                const titleLink = document.createElement('a');
                titleLink.className = 'inquiry-title';
                titleLink.href = `/tour/${inquiry.item.id}`;
                titleLink.textContent = inquiry.title;
                box.appendChild(titleLink);

                // 내용
                const content = document.createElement('p');
                content.className = 'inquiry-content';
                content.textContent = inquiry.content;
                box.appendChild(content);

                // 답변 여부
                const status = document.createElement('span');
                status.className = 'answer-status';
                if (inquiry.isAnswered === 'N') {
                    status.classList.add('pending');
                    status.textContent = '답변 전';
                } else {
                    status.textContent = '답변 완료';
                }
                box.appendChild(status);

                // 박스 추가
                inquiryList.appendChild(box);
            });
        })
        .catch(error => {
            console.error('Error fetching inquiries:', error);
        });
});