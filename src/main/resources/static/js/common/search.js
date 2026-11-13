function createSearchBox(fields, onSearch){
    const searchBox = document.getElementById("searchBox");

    searchBox.innerHTML = "";

    const form = document.createElement("form");
    form.className = "search-box";

    fields.forEach(field => {
        let input;

        if(field.type === "select"){
            input = document.createElement("select");

            const defaultOption = document.createElement("option");
            defaultOption.value = "";
            defaultOption.textContent = field.placeholder ?? "전체";
            input.appendChild(defaultOption);

            (field.options ?? []).forEach(option => {
                const optionEl = document.createElement("option");
                optionEl.value = option.value;
                optionEl.textContent = option.label;
                input.appendChild(optionEl);
            });
        } else {
            input = document.createElement("input");
            input.type = field.type ?? "text";
            input.placeholder = field.placeholder ?? "";
        }

        input.name = field.name;
        form.appendChild(input);
    });

    const searchBtn = document.createElement("button");
    searchBtn.type = "submit";
    searchBtn.textContent = "검색";

    const resetBtn = document.createElement("button");
    resetBtn.type = "reset";
    resetBtn.textContent = "초기화";

    form.appendChild(searchBtn);
    form.appendChild(resetBtn);

    form.addEventListener("submit", event => {
        event.preventDefault();

        const params = new URLSearchParams(new FormData(form));

        for(const [key, value] of params) {
            if(value.trim() === "") {
                params.delete(key);
            }
        }

        onSearch(params);
    });
    searchBox.appendChild(form);
}