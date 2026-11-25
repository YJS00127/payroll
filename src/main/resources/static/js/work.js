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
        {name: "weeklyHolidayDays"}
    ]);
}

document.addEventListener("DOMContentLoaded", () => {
    createSearchBox([
        {name: "keyword", placeholder: "사원명 또는 사원코드 검색"},
        {name: "year", placeholder: "연도"},
        {name: "month", placeholder: "월"}
    ], params => {
        loadWorkData(params)
    })
});