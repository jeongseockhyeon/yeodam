document.addEventListener('DOMContentLoaded', function () {

    const allReviewsModal = document.getElementById('all-reviews-modal');
    const allReviewsContent = document.getElementById('all-reviews-content');
    const closeButton = allReviewsModal.querySelector('.close-btn');

    let reviewContainer = document.getElementsByClassName('review-container')[0];
    const totalRateElement = document.getElementById('totalRate');
    const totalCountElement = document.getElementById('totalCount');

    let limit = 6;
    let splitUrl = window.location.pathname.split("/");
    let itemId = splitUrl[2];
    let url = `/api/${itemId}/reviews?limit=${limit}`;

    // 초기화 작업
    loadReviews();

    async function loadReviews() {

        try {
            let response = await fetch(url);
            if (!response.ok) {
                throw new Error("예외발생");
            }

            let data = await response.json();
            const {reviews, totalRate, totalCount, hasNext} = data;

            totalRateElement.textContent = totalRate;
            totalCountElement.textContent = totalCount;

            let reviewList = document.createElement('div');
            reviewList.id = 'review-list';
            reviewList.classList.add('review-list');


            reviews.forEach(review => {
                const reviewHtml = createReviewItem(review);
                reviewList.insertAdjacentHTML('beforeend', reviewHtml);
            });

            reviewContainer.appendChild(reviewList);

            if (hasNext) {
                let allViewsBtn = document.createElement('button');

                allViewsBtn.classList.add('view-all-btn');
                allViewsBtn.id = 'view-all-btn';
                allViewsBtn.textContent = '후기 모두 보기'


                allViewsBtn.addEventListener('click', async () => {
                    allReviewsContent.innerHTML = ''; // 내용 초기화
                    await fetchReviews(); // 초기 리뷰 불러오기
                    allReviewsModal.style.display = 'flex';
                });

                reviewContainer.appendChild(allViewsBtn);
            }
        } catch (error) {
            console.error(error.message);
        }
    }

    function createReviewItem(review) {
        const {nickName, rate, description, imgPaths} = review;

        const imageTag = imgPaths.length > 0 ? `<img src="${imgPaths[0]}" alt="리뷰 이미지">` : '';

        return `
                <div class="review-item">
                    <div>
                        <div class="rating">${renderStars(rate)} (${rate})</div>
                        <div class="content">${description}</div>
                        <div class="user">${nickName}</div>
                    </div>
                    ${imageTag}
                </div>
            `;
    }

    // 별점 렌더링 함수
    function renderStars(rate) {
        const fullStar = '<span class="stars">★</span>';
        const halfStar = '<span class="stars">☆</span>';
        const emptyStar = '<span class="stars">☆</span>';

        let starsHTML = '';

        const fullStars = Math.floor(rate);
        const hasHalfStar = rate % 1 >= 0.5;
        const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

        // 별 HTML 생성
        starsHTML += fullStar.repeat(fullStars);
        if (hasHalfStar) {
            starsHTML += halfStar;
        }
        starsHTML += emptyStar.repeat(emptyStars);

        return starsHTML;
    }

    async function fetchReviews() {
        let response = await fetch(url);
        if (!response.ok) {
            alert("에러발생");
            return;
        }

        let data = await response.json();

        addReviews(data.reviews);
        document.body.style.overflowY = 'hidden';

        // '다음 리뷰' 버튼 처리
        if (data.hasNext) {
            limit += 6;
            url = `/api/${itemId}/reviews?limit=${limit}`;
            addNextButton();
        } else {
            const existingButton = allReviewsContent.querySelector('.next-btn');
            if (existingButton) {
                allReviewsContent.removeChild(existingButton);
            }
        }
    }

    function addReviews(reviews) {
        reviews.forEach((review, reviewIndex) => {
            const { nickName, rate, description, imgPaths } = review;

            // 캐러셀 요소 생성
            let carousel = '';
            if (imgPaths && imgPaths.length > 0) {
                const carouselId = `carousel-review-${reviewIndex}`;
                const carouselItems = imgPaths.map((img, imgIndex) => `
                <div class="carousel-item ${imgIndex === 0 ? 'active' : ''}">
                    <img src="${img}" class="d-block w-100" alt="Review Image">
                </div>
            `).join('');

                const carouselControls = imgPaths.length > 1 ? `
                <button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Next</span>
                </button>
            ` : '';

                carousel = `
                <div id="${carouselId}" class="carousel slide" data-bs-ride="false">
                    <div class="carousel-inner">
                        ${carouselItems}
                    </div>
                    ${carouselControls}
                </div>
            `;
            }

            // 리뷰 카드 생성
            const reviewDiv = document.createElement('div');
            reviewDiv.innerHTML = `
            <h3>${nickName}</h3>
            <div class="modal-rating">${renderStars(rate)} (${rate})</div>
            <p>${description}</p>
            ${carousel}
            <hr>
        `;

            // 부모 컨테이너에 추가
            allReviewsContent.appendChild(reviewDiv);
        });
    }


    /*function addReviews(reviews) {
        reviews.forEach(review => {
            const {nickName, rate, description, imageUrl} = review;
            const imageTag = imageUrl ? `<img src="${imageUrl}" alt="리뷰 이미지">` : '';
            const reviewDiv = document.createElement('div');
            reviewDiv.innerHTML = `
                <h3>${nickName}</h3>
                <div class="modal-rating">${renderStars(rate)} (${rate})</div>
                <p>${description}</p>
                ${imageTag}
                <hr>
            `;
            allReviewsContent.appendChild(reviewDiv);
        });
    }*/

    function addNextButton() {
        const existingButton = allReviewsContent.querySelector('.next-btn');
        if (existingButton) {
            allReviewsContent.removeChild(existingButton);
        }

        const nextButton = document.createElement('button');
        nextButton.textContent = '다음 리뷰';
        nextButton.classList.add('next-btn');

        nextButton.addEventListener('click', async () => {
            const currentScrollPosition = window.scrollY;
            await fetchReviews();
            window.scrollTo(0, currentScrollPosition);
        });

        allReviewsContent.appendChild(nextButton); // 새로운 버튼 추가
    }

    document.addEventListener('keyup', (e) => {
        if (e.code === 'Escape') {
            close();
        }
    })

    closeButton.addEventListener('click', () => {
        close();
    });

    close = () => {
        limit = 6;
        url = `/api/${itemId}/reviews?limit=${limit}`;
        allReviewsModal.style.display = 'none';
        document.body.style.overflowY = 'auto';
    }
});