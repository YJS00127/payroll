let searchParams = new URLSearchParams();

async function loadInsuranceData(params = searchParams){
    searchParams = new URLSearchParams(params);

    const response = await fetch(`/api/insurance-rate?${searchParams.toString()}`);
    const data = await response.json();

    const insuranceData = data.map(insurance => ({
        ...insurance,
        nationalPensionRate: `${insurance.nationalPensionRate}%`,
        healthInsuranceRate: `${insurance.healthInsuranceRate}%`,
        longTermCareInsuranceRate: `${insurance.longTermCareInsuranceRate}%`,
        employmentInsuranceRate: `${insurance.employmentInsuranceRate}%`,
        localIncomeTaxRate: `${insurance.localIncomeTaxRate}%`
    }))

    renderTable(".insurance-list", insuranceData, [
        {name: "id", type: "checkbox", className: "insurance-checkbox"},
        {name: "effectiveDate", label: "반영일"},
        {name: "nationalPensionRate", label: "국민연금 공제율"},
        {name: "healthInsuranceRate", label: "건강보험 공제율"},
        {name: "longTermCareInsuranceRate", label: "노인장기요양 공제율"},
        {name: "employmentInsuranceRate", label: "고용보험 공제율"},
        {name: "localIncomeTaxRate", label: "주민세 공제율"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick: insurance => editInsurance(insurance)},
                {label: "삭제", onClick: insurance => delInsurance(insurance.id)}
            ]
        }
    ]);
}

function addInsurance(){
    openModal({
        title: "공제율 등록",
        fields: [
            {
                name: "year",
                label: "반영 해",
                type: "number",
                max: 3000,
                min: 1900
            },
            {
                name: "month",
                label: "반영 월",
                type: "number",
                max: 12,
                min: 1

            },
            {
                name: "nationalPensionRate",
                label: "국민연금 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "healthInsuranceRate",
                label: "건강보험 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {name: "longTermCareInsuranceRate",
                label: "노인장기요양 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "employmentInsuranceRate",
                label: "고용보험 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "localIncomeTaxRate",
                label: "주민세 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
        ],
        onSubmit: async data => {
            await saveInsurance(data);
            await loadInsuranceData();
        }
    })
}

async function saveInsurance(data){
    const response = await fetch(`/api/insurance-rate`, {
        method: "POST",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "공제율 등록 실패");
    }

    alert(result.message);
}

async function editInsurance(insurance){
    const response = await fetch(`/api/insurance-rate/${insurance.id}`)
    const insuranceData = await response.json();

    openModal({
        title: "공제율 수정",
        fields: [
            {
                name: "year",
                label: "반영 연도",
                type: "number",
                min: 1900,
                max: 2100
            },
            {
                name: "month",
                label: "반영 월",
                type: "number",
                min: 1,
                max: 12
            },
            {
                name: "nationalPensionRate",
                label: "국민연금 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "healthInsuranceRate",
                label: "건강보험 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "longTermCareInsuranceRate",
                label: "노인장기요양 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "employmentInsuranceRate",
                label: "고용보험 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            },
            {
                name: "localIncomeTaxRate",
                label: "주민세 공제율",
                type: "number",
                min: 0,
                max: 99,
                step: 0.0001
            }
        ],
        values: {
            ...insuranceData,
            year: insuranceData.effectiveDate.slice(0,4),
            month: Number(insuranceData.effectiveDate.slice(5,7))
        },
        onSubmit: async data => {
            await updateInsurance(insurance.id, data);
            await loadInsuranceData(searchParams);
        }
    })
}

async function updateInsurance(id, data){
    const response = await fetch(`/api/insurance-rate/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "공제율 수정 실패")
    }
    alert(result.message);
}

async function delInsurance(id){
    if(!confirm("해당 공제율 정보를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/insurance-rate/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "공제율 삭제 실패")
    }

    alert(result.message);
    await loadInsuranceData();
}

async function delSelectedInsurance(){
    const ids = [...document.querySelectorAll(".insurance-checkbox:checked")]
        .map(checkbox => Number(checkbox.value))

    if(ids.length !== 0){
        if(!confirm("선택한 공제율 정보를 삭제하시겠습니까?")) return;
    } else {
        alert("삭제할 공제율 정보를 선택해주세요")
        return;
    }

    const response = await fetch(`/api/insurance-rate`, {
        method: "DELETE",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message || "공제율 삭제 실패")
    }

    alert(result.message);
    await loadInsuranceData();
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("selectAll").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".insurance-checkbox")

        checkboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        })
    })

    document.querySelector(".insurance-list").addEventListener("change", () => {
        const checkboxes = document.querySelectorAll(".insurance-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);
    })

    createSearchBox([
        {name: "year", type: "number", placeholder: "연도"},
        {name: "month", type: "number", placeholder: "월"}
    ], params => loadInsuranceData(params));
    loadInsuranceData();
})