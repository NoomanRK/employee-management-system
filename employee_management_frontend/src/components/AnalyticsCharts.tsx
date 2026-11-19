
import {
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid
} from "recharts";

import type { SalaryOverview } from "../types/analytics";
import type { DepartmentDistribution } from "../types/analytics";

import "./AnalyticsCharts.css";

interface AnalyticsChartsProps {
    departmentDistribution: DepartmentDistribution[];
    salaryOverview: SalaryOverview[];
}

const COLORS = [
    "#3B82F6",
    "#10B981",
    "#F59E0B",
    "#8B5CF6",
    "#EF4444",
    "#06B6D4",
    "#EC4899"
];

function AnalyticsCharts({
    departmentDistribution,
    salaryOverview,
}: AnalyticsChartsProps) {

    return (
        <section className="analytics-charts">

            {/* Department Distribution */}
            <div className="chart-card">
                <div className="chart-header">
                    <h3>Department Distribution</h3>
                    <p>Employees across all departments</p>
                </div>

                {departmentDistribution.length > 0 ? (
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={departmentDistribution}
                                dataKey="employeeCount"
                                nameKey="department"
                                cx="50%"
                                cy="50%"
                                innerRadius={75}
                                outerRadius={110}
                                paddingAngle={3}
                            >
                                {departmentDistribution.map((entry, index) => (
                                    <Cell
                                        key={entry.department}
                                        fill={COLORS[index % COLORS.length]}
                                    />
                                ))}
                            </Pie>

                            <Tooltip />
                            <Legend />
                        </PieChart>
                    </ResponsiveContainer>
                ) : (
                    <p className="chart-empty">
                        No department data available.
                    </p>
                )}
            </div>

            {/* Salary Overview */}
            <div className="chart-card">
                <div className="chart-header">
                    <h3>Salary Overview</h3>
                    <p>Employee salary comparison on this page</p>
                </div>

                {salaryOverview.length > 0 ? (
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={salaryOverview}>
                            <CartesianGrid
                                strokeDasharray="3 3"
                                vertical={false}
                            />

                            <XAxis
                                dataKey="firstName"
                                tick={{ fontSize: 12 }}
                                interval={0}
                                angle={-25}
                                textAnchor="end"
                                height={75}
                            />

                            <YAxis
                                tick={{ fontSize: 12 }}
                                tickFormatter={(value) =>
                                    `₹${Number(value).toLocaleString("en-IN")}`
                                }
                            />

                            <Tooltip
                                formatter={(value) => [
                                    `₹${Number(value).toLocaleString("en-IN")}`,
                                    "Salary"
                                ]}
                            />

                            <Bar
                                dataKey="salary"
                                fill="#3B82F6"
                                radius={[6, 6, 0, 0]}
                            />
                        </BarChart>
                    </ResponsiveContainer>
                ) : (
                    <p className="chart-empty">
                        No salary data available.
                    </p>
                )}
            </div>

        </section>
    );
}

export default AnalyticsCharts;
