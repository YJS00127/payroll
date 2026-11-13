function createPagination(pageData, onPageChange) {
    const pagination = document.querySelector(".pagination");

    pagination.innerHTML = "";

    const currentPage = pageData.number;
    const totalPages = pageData.totalPages;
    const pageGroupSize = pageData.pageable.pageSize;

    if(totalPages === 1 || pageDate === null) return;

    const startPage = Math.floor(currentPage / pageGroupSize) * pageGroupSize;
    const endPage = Math.min(startPage + pageGroupSize, totalPages);

    function addButton(text, page, disabled = false, active = false){
        const button = document.createElement("button");

        button.type = "button";
        button.textContent = text;
        button.disabled = disabled;

        if(active) {
            button.classList.add("active");
        }

        button.addEventListener("click", () => {
            onPageChange(page);
        });

        pagination.appendChild(button);
    }

    addButton("<", startPage -1, startPage === 0);
    addButton("이전", currentPage - 1, currentPage === 0);

    for(let i = startPage; i < endPage; i++){
        addButton(i + 1, i, false, i === currentPage);
    }

    addButton("다음", currentPage + 1, currentPage === (totalPages -1));
    addButton(">", endPage, endPage >= totalPages);
}