// 부서 목록 조회
async function loadDeptData() {
    const response = await fetch("/api/dept")
    const deptData = await response.json();

    renderTable(".dept-list", deptData, [
        {name: "id", type: "checkbox", className: "dept-checkbox"},
        {name: "name"},
        {name: "code"},
        {name: "createdDeptDate"},
        {
            type: "buttons",
            button: [
                {label: "수정", onClick: dept => editDept(dept)},
                {label: "삭제", onClick: dept => delDept(dept)}
            ]
        }
    ]);
}

function addDept() {
    openModal({
        title: "부서 등록",
        fields: [
            {name: "name", label: "부서명", required: true},
            {name: "code", label: "부서코드", required: true},
            {
                name: "createdDeptDate",
                label: "부서 생성일",
                type: "date",
                required: true
            }
        ],
        onSubmit: async data => {
            await saveDept(data);
            await loadDeptData();
        }
    })
}

async function saveDept(data){
    const response = await fetch(`/api/dept`, {
        method: "POST",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail") {
        throw new Error("부서 등록 실패");
    }

    alert(result.message);
}

async function editDept(dept){
    console.log(dept.id)
    const response = await fetch(`/api/dept/${dept.id}`);
    const deptData = await response.json();
    openModal({
        title: "부서 수정",
        fields: [
            {name: "name", label: "부서명", required: true},
            {name: "code", label: "부서코드", required: true},
            {
                name: "createdDeptDate",
                label: "부서생성일",
                type: "date",
                required: true
            }
        ],
        values: deptData,
        onSubmit: async data => {
            await updateDept(dept.id, data);
            await loadDeptData();
        }
    })
}

async function updateDept(id, data){
    const res = await fetch(`/api/dept/${id}`, {
        method: "PUT",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    });
    const result = await res.json();

    if(!res.ok || result.result === "fail"){
        throw new Error(result.message || "부서 수정 실패");
    }
    alert(result.message);
}

// 부서 단일 삭제
async function delDept(id){
    if(!confirm("해당 부서를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/dept/${id}`, {
        method: "DELETE"
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "부서 삭제 실패");
    }

    alert(result.message);
    await loadDeptData();
}

// 부서 다중 삭제
async function delSelectedDept(){
    const ids = [...document.querySelectorAll(".dept-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length !== 0) {
        if (!confirm("선택된 부서를 삭제하시겠습니까?")) return;
    } else{
        alert("삭제할 부서를 선택해주세요");
        return;
    }

    const response = await fetch(`/api/depts`, {
        method: "DELETE",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "부서 삭제 실패");
    }

    alert(result.message);
    await loadDeptData();
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("selectAll").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".dept-checkbox");

        checkboxes.forEach(checkboxes => {
            checkboxes.checked = this.checked;
        });
    });

    document.querySelector(".dept-list").addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".dept-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    })
    loadDeptData();
});