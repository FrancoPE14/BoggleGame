import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: "http://163.192.206.210:8080/api/:path*",
            },
        ];
    },
};

export default nextConfig;
