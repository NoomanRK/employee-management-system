
import api from "./api";
import type {
    DepartmentDistribution,
    AnalyticsData,
    SalaryOverview
} from "../types/analytics";

export const getTotalEmployees = async (): Promise<number> => {
    const response = await api.get<number>(
        "/analytics/total-employees"
    );

    return response.data;
};

export const getAverageSalary = async (): Promise<number> => {
    const response = await api.get<number>(
        "/analytics/average-salary"
    );

    return response.data;
};

export const getDepartmentDistribution = async (): Promise<DepartmentDistribution[]> => {
    const response = await api.get<DepartmentDistribution[]>(
        "/analytics/department-distribution"
    );

    return response.data;
};

export const getAnalyticsData = async (): Promise<AnalyticsData> => {
    const [
        totalEmployees,
        averageSalary,
        departmentDistribution
    ] = await Promise.all([
        getTotalEmployees(),
        getAverageSalary(),
        getDepartmentDistribution()
    ]);

    return {
        totalEmployees,
        averageSalary,
        departmentDistribution
    };
};

export const getSalaryOverview = async (): Promise<SalaryOverview[]> => {
    const response = await api.get<SalaryOverview[]>(
        "/analytics/salary-overview"
    );

    return response.data;
};
