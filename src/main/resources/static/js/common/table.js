function renderTable(tbodyClass, data, columns  ){
    const tbody = document.querySelector(tbodyClass);
    tbody.innerHTML = "";

    data.forEach(item => {
        const tr = document.createElement("tr");

        columns.forEach(column => {
            const td = document.createElement("td");

            if(column.type === "checkbox"){
                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.className = column.className;
                checkbox.value = item[column.name];
                td.appendChild(checkbox);
            } else if(column.type === "button"){
                column.buttons.forEach(buttonInfo => {
                    const button = document.createElement("button");

                    button.type = "button";
                    button.textContent = buttonInfo.label;
                    button.addEventListener("click", () => buttonInfo.onClick(item));

                    td.appendChild(button);
                });
            } else {
                td.textContent = item[column.name] ?? "";
            }
            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    })
}