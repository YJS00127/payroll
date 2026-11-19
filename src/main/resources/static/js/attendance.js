let searchParams = new URLSearchParams();

const statusOptions = [
    {value: "출근", label: "출근"},
    {value: "지각", label: "지각"},
    {value: "조퇴", label: "조퇴"},
    {value: "결근", label: "결근"},
    {value: "휴가", label: "휴가"},
    {value: "반차", label: "반차"},
    {value: "병가", label: "병가"}
]

// 근태 데이터
async function loadAttendanceData(page = 0, params = searchParams) {
    searchParams = new URLSearchParams(params);
    searchParams.set("page", page);

    const response = await fetch(`/api/attendance?${searchParams.toString()}`);
    const pageData = await response.json();

    if (!response.ok) {
        throw new Error(pageData.message || "근태 조회 실패");
    }
    const attendanceList = pageData.content.map(attendance => {
        const totalMinutes = attendance.workMinutes + attendance.overtimeWorkMinutes + attendance.holidayWorkMinutes;

        return{
            ...attendance,
            workTime: attendance.checkIn && attendance.checkOut
                ? `${attendance.checkIn.slice(0, 5)} ~ ${attendance.checkOut.slice(0, 5)}` : "-",
            totalWorkHour: `${Math.floor(totalMinutes / 60)}시간${totalMinutes % 60 === 0 ? "" : ` ${totalMinutes % 60}분`}`,
            status: attendance.holidayWorkMinutes > 0 ? "휴일근로" : attendance.status
        };
    });

    renderTable(".attendance-list", attendanceList, [
        {name: "id", type: "checkbox", className: "attendance-checkbox"},
        {name: "empName", label: "사원명"},
        {name: "empCode", label: "사원코드"},
        {name: "workDate", label: "근로 날짜"},
        {name: "workTime", label: "근로 시간", type: "number"},
        {name: "totalWorkHour", label: "총 근로 시간", type: "number"},
        {name: "status", label: "근로 상태"},
        {
            type: "button",
            buttons: [
                {label: "수정", onClick : attendance => editAttendance(attendance)},
                {label: "삭제", onClick : attendance => delAttendance(attendance.id)}
            ]
        }]
    );

    document.getElementById("totalAttendanceCount").textContent =
        `총 ${pageData.totalElements}건`;

    createPagination(pageData, nextPage => loadAttendanceData(nextPage));
}

async function addAttendance(){
    const keyword = searchParams.get("keyword");
    let empOptions = [];

    if(keyword) {
        const response = await fetch(`/api/emp?keyword=${keyword}`);
        const empData = await response.json();

        empOptions = empData.content.map(emp => ({
            value: emp.code,
            label: `${emp.name} (${emp.code})`
        }))
    }

    openModal({
        title: "근태 등록",
        fields: [
            {
                name: "empCode",
                label: keyword ? "사원 선택" : "사원코드",
                type: keyword ? "select" : "text",
                options: empOptions,
                fullWidth: true
            },
            {name: "workDate", label: "근로일", type: "date"},
            {
                name: "status",
                label: "근로 상태",
                type: "select",
                options: statusOptions
            },
            {name: "checkIn", label: "출근 시간", type: "time"},
            {name: "checkOut", label: "퇴근 시간", type: "time"}
        ],
        values: {
            workDate: new Date().toLocaleDateString("sv-SE"),
            status: "출근",
            checkIn: "09:00",
            checkOut: "18:00"
        },
        onSubmit: async data => {
            await saveAttendance(data)
        }
    })
}

async function saveAttendance(data){
    const response = await fetch(`/api/attendance`, {
        method: "POST",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(data)
    });
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message)
    }
    alert(result.message);
    await loadAttendanceData();
}

// 근태 수정
async function editAttendance(attendance){
    const response = await fetch(`/api/attendance/${attendance.id}`);
    const attendanceData = await response.json();
    openModal({
        title: "근태 수정",
        fields: [
            {name: "workDate", label: "근로일", type: "date"},
            {
                name: "status",
                label: "근로 상태",
                type: "select",
                options: statusOptions,
            },
            {
                name: "checkIn",
                label: "출근 시간",
                type: "time",
                min: "06:00",
                max: "15:00"
            },
            {name: "checkOut", label: "퇴근 시간", type: "time"}
        ],
        values: attendanceData,
        onSubmit: async data => {
            await updateAttendance(attendance.id, data, attendanceData.empCode);
            await loadAttendanceData(searchParams);
        }
    })
}

async function updateAttendance(id, data, empCode){
    const res = await fetch(`/api/attendance/${id}`, {
        method: "PUT",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify({
            ...data,
            empCode: empCode
        })
    });
    const result = await res.json();

    if(!res.ok || result.result === "fail"){
        throw new Error(result.message || "근태 수정 실패");
    }

    alert(result.message);
}

// 근태 삭제 (attendance)
async function delAttendance(id){
    if(!confirm("해당 근태를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/attendance/${id}`, {
        method: "DELETE"
    });
    const result = await response.json();

    alert(result.message);
    await loadAttendanceData(searchParams);
}

// 근태 다중 삭제 (attendance)
async function delSelectedAttendance(){
    const ids = [...document.querySelectorAll(".attendance-checkbox:checked")]
        .map(checkbox => Number(checkbox.value));

    if(ids.length !== 0) {
        if (!confirm("선택된 근태를 삭제하시겠습니까?")) return;
    } else{
        alert("삭제할 근태를 선택해주세요");
        return;
    }

    const response = await fetch(`/api/attendances`, {
        method: "DELETE",
        headers: {"Content-Type" : "application/json"},
        body: JSON.stringify(ids)
    })
    const result = await response.json();

    if(!response.ok || result.result === "fail"){
        throw new Error(result.message);
    }

    alert(result.message);
    await loadAttendanceData(searchParams);
}

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("selectAll").addEventListener("change", function() {
        const checkboxes = document.querySelectorAll(".attendance-checkbox");

        checkboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });

    document.querySelector(".attendance-list").addEventListener("change",() => {
        const checkboxes = document.querySelectorAll(".attendance-checkbox");
        const selectAll = document.getElementById("selectAll");

        selectAll.checked = checkboxes.length > 0 &&
            [...checkboxes].every(checkbox => checkbox.checked);

    });

    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => {
        loadAttendanceData(0, params);
    });
});