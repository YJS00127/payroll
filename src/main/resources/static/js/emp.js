let searchParams = new URLSearchParams();
// 사원 목록 조회
async function loadEmpData(page = searchParams.get("page") ?? 0, params = searchParams) {
    searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const response = await fetch(`/api/emp?${searchParams.toString()}`);
    const pageData = await response.json();

    // 사원 목록 출력
    renderTable(".employee-list", pageData.content, [
        {name: "id", type: "checkbox", className: "emp-checkbox"},
        {name: "code"},
        {name: "name"},
        {name: "deptName"},
        {name: "rank"},
        {name: "joiningDate"},
        {name: "hourlyWage"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick: emp => editEmp(emp)},
                {label: "삭제", onClick: emp => delEmp(emp.id)}
            ]
        }
    ]);

    // 전체 사원 수 출력
    document.getElementById("totalCount").textContent =
        `총 ${pageData.totalElements}명`;

    // 페이지네이션 출력
    createPagination(pageData, nextPage => loadEmpData(nextPage));
}

// 성별 옵션
const genderOptions = [
    {value: "남", label: "남"},
    {value: "여", label: "여"}
]

// 직급 옵션
const rankOptions = [
    {value: "부장", label: "부장"},
    {value: "차장", label: "차장"},
    {value: "과장", label: "과장"},
    {value: "대리", label: "대리"},
    {value: "사원", label: "사원"}
]

let deptOptions;

// 사원 등록 팝업
function addEmp() {
    openModal({
        title: "사원 등록",
        fields: [
            {name: "name", label: "이름"},
            {name: "code", label: "사원코드"},
            {
                name: "deptName",
                label: "부서명",
                type: "select",
                options: deptOptions
            },
            {
                name: "rank",
                label: "직급",
                type: "select",
                options: rankOptions
            },
            {name: "joiningDate", label: "입사일", type: "date"},
            {name: "birth", label: "생년월일", type: "date"},
            {
                name: "gender",
                label: "성별",
                type: "select",
                options: genderOptions
            },
            {
                name: "hourlyWage",
                label: "시급",
                type: "number",
                min: 9000,
                max: 50000
            },
            {
                name: "initialMonthlyIncome",
                label: "월평균 급여",
                type: "number",
                min: 500000,
                max: 5000000
            },
            {
                name: "contractDailyWorkMinutes",
                label: "소정근로시간",
                type: "number",
                min: 1,
                max: 10
            }
        ],
        onSubmit: async data => {
            await saveEmp(data);
            await loadEmpData();
        }
    });
}
// 사원 등록
async function saveEmp(data){
    const response = await fetch("/api/emp", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.maessage || "사원 등록 실패");
    }

    alert(result.message);
}

// 사원 수정 팝업
async function editEmp(emp) {
    const response = await fetch(`/api/emp/${emp.id}`);
    const empData = await response.json();

    openModal( {
        title: "사원 수정",
        fields: [
            {name: "name", label: "이름"},
            {name: "code", label: "사원코드"},
            {
                name: "deptName",
                label: "부서명",
                type: "select",
                options: deptOptions
            },
            {
                name: "rank",
                label: "직급",
                type: "select",
                options: rankOptions
            },
            {name: "joiningDate", label: "입사일", type: "date"},
            {name: "birth", label: "생년월일", type: "date"},
            {
                name: "gender",
                label: "성별",
                type: "select",
                options: genderOptions
            },
            {
                name: "contractDailyWorkHour",
                label: "소정근로시간",
                type: "number",
                min: 1,
                max: 10
            },
            {
                name: "initialMonthlyIncome",
                label: "월평균 급여",
                type: "number",
                min: 500000,
                max: 5000000
            },
            {
                name: "hourlyWage",
                label: "시급",
                type: "number",
                disabled: true
            },
        ],
        values: {
            ...empData,
            contractDailyWorkHour : empData.contractDailyWorkMinutes / 60
        },
        onSubmit: async data => {
            await updateEmp(emp.id, data);
            await loadEmpData(searchParams.get("page") ?? 0, searchParams);
        }
    })
}

// 사원 정보 수정
async function updateEmp(id, data){
    const response = await fetch(`/api/emp/${id}`, {
      method: "PUT",
      headers: {"Content-Type" : "application/json"},
      body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "사원 수정 실패");
    }
    alert(result.message);
}

// 사원 단일 삭제
async function delEmp(id){
    if(!confirm("해당 사원을 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/emp/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail") {
        throw new Error(result.message || "사원 삭제 실패");
    }

    alert(result.message);
    await loadEmpData(searchParams);
}

// 사원 다중 삭제
async function delSelectedEmp(){
    const ids = [...document.querySelectorAll(".emp-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length !== 0) {
        if (!confirm("선택된 사원을 삭제하시겠습니까?")) return;
    } else{
        throw new Error("삭제할 사원을 선택해주세요");
    }

    const response = await fetch(`api/emp`, {
        method: "DELETE",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "부서 삭제 실패");
    }

    alert(result.message);
    await loadEmpData(searchParams);
}

// 최초 페이지 로딩
document.addEventListener("DOMContentLoaded", async () => {
    const response = await fetch("/api/dept");
    const deptData = await response.json();

    deptOptions = deptData.map(dept => ({
        value: dept.name,
        label: dept.name
    }));
    // 전체 선택 시 모든 행 선택, 해제

    document.getElementById("selectAll").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".emp-checkbox");

        checkboxes.forEach(checkboxes => {
            checkboxes.checked = this.checked;
        })
    })
    // 모든 행 선택 시 전체 선택 활성화, 하나라도 해제 시 전체 선택 취소
    document.querySelector(".employee-list").addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".emp-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    })

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {
            name: "deptName",
            type: "select",
            placeholder: "부서 전체",
            options: deptOptions
        }
    ], params => {
        loadEmpData(0, params)
    })
})
