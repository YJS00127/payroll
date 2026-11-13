function openModal({title, fields, values = {}, onSubmit}) {
    const modal = document.getElementById("modal");
    const container = document.getElementById("modalFields");

    document.getElementById("modalTitle").textContent = title;
    container.innerHTML = "";

    modalSubmitHandler = onSubmit;

    fields.forEach(field => {
        const div = document.createElement("div");
        div.className = "modal-field";

        const label = document.createElement("label");
        label.textContent = field.label;

        let input;

        if(field.type === "select") {
            input = document.createElement("select");

            input.add(new Option("선택하세요", ""));

            (field.options ?? []).forEach(options => {
                input.add(new Option(options.label, options.value));
            });
        } else if(field.type === "textarea") {
            input = document.createElement("textarea");
        } else {
            input = document.createElement("input");
            input.type = field.type ?? "text";
        }

        input.name = field.name;
        input.id = `modal-${field.name}`;
        input.required = field.required ?? false;
        input.readOnly = field.readOnly ?? false;
        input.value = values[field.name] ?? field.value ?? "";

        if(field.type === "checkbox"){
            input.checked = values[field.name] ?? field.value ?? false;
        }

        label.htmlFor = input.id;

        div.appendChild(label);
        div.appendChild(input);
        container.appendChild(div);
    });

    modal.hidden = false;
}

function closeModal() {
    document.getElementById("modal").hidden = true;
    modalSubmitHandler = null;
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("modalForm");
    const modal = document.getElementById("modal");

    if (!form || !modal) return;

    document.getElementById("modalCancel")
        .addEventListener("click", closeModal);

    form.addEventListener("submit", async event => {
        event.preventDefault();

        const data = Object.fromEntries(new FormData(form));

        form.querySelectorAll('input[type="checkbox"]').forEach(input => {
            data[input.name] = input.checked;
        });

        const saveBtn = document.getElementById("modalSave");
        saveBtn.disabled = true;

        try {
            await modalSubmitHandler(data);
            closeModal();
        } catch (error) {
            alert(error.message);
        } finally {
            saveBtn.disabled = false;
        }
    });
});