import type { NextConfig } from "next";

const rawBackendBaseUrl = process.env.BACKEND_BASE_URL?.trim();
const backendBaseUrl = (rawBackendBaseUrl || "http://127.0.0.1:8080").replace(/\/$/, "");

const nextConfig: NextConfig = {
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: `${backendBaseUrl}/api/:path*`,
            },
        ];
    },
};

export default nextConfig;
