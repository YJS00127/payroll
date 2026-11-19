let searchParams = new URLSearchParams();
async function loadWorkData(params = searchParams){
    searchParams = new URLSearchParams(params);

    const response = await fetch(`/api/work-history?${searchParams.toString()}`);
    const data = await response.json();

    const workData = data.map(work => {
        const totalMinutes = work.totalWorkMinutes + work.overtimeWorkMinutes + work.holidayWorkMinutes;
        return {
            ...work,
            workYearMonth: `${work.year}.${work.month < 10 ? `0${work.month}` : `${work.month}`}`,
            totalWorkHour:
                `${totalMinutes / 60}시간${totalMinutes % 60 === 0 ? "" : ` ${totalMinutes % 60}분`}`,
            overtimeWorkHour: work.overtimeWorkMinutes !== 0 ?
                `${work.overtimeWorkMinutes / 60}시간${work.overtimeWorkMinutes % 60 === 0 ? "" : ` ${work.overtimeWorkMinutes % 60}분`}` : "-",
            holidayWorkHour: work.holidayWorkMinutes !== 0 ?
                `${work.holidayWorkMinutes / 60}시간${work.overtimeWorkMinutes % 60 === 0 ? "" : ` ${work.overtimeWorkMinutes % 60}분`}` : "-"
        }
    });

    renderTable(".work-list", workData, [
        {name: "workYearMonth"},
        {name: "empName"},
        {name: "empCode"},
        {name: "workDays"},
        {name: "totalWorkHour"},
        {name: "overtimeWorkHour"},
        {name: "holidayWorkHour"},
        {name: "weeklyHolidayDays"},
        // {
        //     type: "button",
        //     // buttons: [
        //     //     {label: "수정", onClick: work => editWork(work)}
        //     // ]
        // }
    ]);
}

document.addEventListener("DOMContentLoaded", () => {
    // loadWorkData(searchParams)
    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => {
        loadWorkData(params)
    })
});

// function addWork(){
//     openModal({
//         title: "월별 근무 등록",
//         fields: [
//             {name: "year", label: "연도", type: "number"},
//             {name: "month", label: "월", type: "number"},
//             {name: "empCode", label: "사원코드"},
//             {name: "workDays", label: "총 근로 일수", type: "number"},
//             {name: "totalWorkHour", label: "총 근로 시간", type: "number"},
//             {name: "overtimeWorkHour", label: "연장근로 시간", type: "number"},
//             {name: "holidayWorkHour", label: "휴일근로 시간", type: "number"},
//             {name: "weeklyHolidayDays", label: "주차일수", type: "number"}
//         ],
//         onSubmit: async data => {
//             await saveWork(data);
//             await loadWorkData(searchParams);
//         }
//     });
// }

// async function saveWork(data){
//     const response = await fetch("/api/work-history", {
//         method: "POST",
//         headers: {"Content-Type" : "application/json"},
//         body: JSON.stringify(data)
//     });
//     const result = response.json();
//
//     if(!response.ok || result.result === "fail"){
//         throw new Error(result.message || "근무 기록 등록 실패")
//     }
//
//     alert(result.message);
// }

// async function editWork(work) {
//     const response = await fetch(`/api/work-history/${work.id}`);
//     const workData = await response.json();
//     openModal({
//         title: "월별 근무 기록 수정",
//         fields: [
//             {name: "empName", label: "사원명", readonly: true},
//             {name: "empCode", label: "사원코드", readonly: true},
//             {
//                 name: "year",
//                 label: "연도",
//                 type: "number",
//                 min: 1900,
//                 max: 2100
//             },
//             {
//                 name: "month",
//                 label: "월",
//                 type: "number",
//                 min: 1,
//                 max: 12
//             },
//             {
//                 name: "totalWorkHour",
//                 label: "총 근로 시간",
//                 type: "number",
//                 min: 1,
//                 max: 500
//             },
//             {
//                 name: "overtimeWorkHour",
//                 label: "연장근로 시간",
//                 type: "number",
//                 max: 150,
//             },
//             {
//                 name: "holidayWorkHour",
//                 label: "휴일근로 시간",
//                 type: "number",
//                 max: 150
//             },
//             {
//                 name: "weeklyHolidayDays",
//                 label: "주차일수",
//                 type: "number",
//                 max: 5
//             }
//         ],
//         values: workData,
//         onSubmit: async data => {
//             await updateWork(work.id, data);
//             await loadWorkData();
//         }
//     })
// }
//
// async function updateWork(id, data){
//     const response = await fetch(`/api/work-history/${id}`, {
//         method: "PUT",
//         headers: {"Content-Type" : "application/json"},
//         body: JSON.stringify(data)
//     });
//
//     const result = response.json();
//
//     if(!response.ok || result.result === "fail"){
//         throw new Error(result.message || "근무 기록 수정 실패");
//     }
//
//     alert(result.message);
// }