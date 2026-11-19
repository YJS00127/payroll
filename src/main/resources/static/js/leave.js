let searchUsageParams = new URLSearchParams();
let searchRecordParams = new URLSearchParams();

async function loadLeaveUsageData(page=0, params = searchUsageParams){
    searchUsageParams = new URLSearchParams(params);
    searchUsageParams.set("page", page);

    const response = await fetch(`/api/leave?${searchUsageParams}`)
    const leaveUsageData = await response.json();

    renderTable(".leave-usage-list", leaveUsageData.content, [
        {name: "empName", label: "사원명"},
        {name: "empCode", label: "사원코드"},
        {name: "leaveDate", label: "휴가 날짜"},
        {name: "usedDays", label: "휴가일수"},
        {
            type: "button",
            buttons:[
                {label: "수정", onClick: leaveUsage => editLeaveUsage(leaveUsage)},
                {label: "삭제", onclick: leaveUsage => delLeaveUsage(leaveUsage.id)}
            ]
        }
    ]);

    createPagination(leaveUsageData, nextPage => loadLeaveUsageData(nextPage));
}

async function addLeaveUsage(){
    openModal({
        title: "휴가 이력 등록",
        fields: [
            {name: "empName", label: "사원명"},
            {name: "empCode", label: "사원코드"},
            {name: "startDate", label: "휴가 시작일", type: "date"},
            {name: "endDate", label: "휴가 종료일", type: "date"}
        ],
        onSubmit: async data => {
            await saveLeaveUsage(data)
            await loadLeaveUsageData(searchUsageParams)
        }
    });
}

async function saveLeaveUsage(data){
    const response = await fetch(`/api/leave-usage`,{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
}