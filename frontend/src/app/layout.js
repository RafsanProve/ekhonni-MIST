import { Montserrat } from "next/font/google";
import "./globals.css";


const montserrat = Montserrat({ subsets: ["latin"] });

import Footer from "@/components/Footer";
import Header from "@/components/Header";
import Search from "@/components/Search";


export const metadata = {
  title: "Ekhonni",
  description: "Generated by create next app",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={montserrat.className}>
          <Header/>
          <Search/>
          {children}
          <Footer/>
      </body>
    </html>
  );
}
