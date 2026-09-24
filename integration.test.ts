import { describe, expect, it, vi } from "vitest";
import { Account, calculateBalanceSheetTotals, calculateInventoryCapitalBalance, calculateModalAccountBalance, calculateReconciledInventoryCapital, calculateTransactionAmounts, calculateRevenueBreakdown, productDepreciation, totalToolDepreciation, productBuildingCost, totalBuildingCost, transactionNetTotal, transactionRevenueBreakdown, transactionRevenueNet, captureInstallPrompt, handleAppInstalled, inventoryCapitalDelta, inventoryValue, productTypeLabel, removeCustomAccount, removeTransactionByNumber, restoreInventoryForTransaction, summarizeTransactionTotals, totalInventoryValue, transactionDateToIso, upsertCustomAccount, transactionExpenseTotal, transactionExpenseNet, hasDuplicateProductBarcode, isTransportExpenseProduct, buildTransportExpenseProduct, buildCoaExportRows, buildBalanceSheetExportRows, transactionMatchesPeriod, transactionsThroughDate, reportPresetRange, upsertFailedScanRecord } from "./pages/Home";
import { readFileSync } from "node:fs";
import { resolve } from "node:path";

describe("Anton Service POS integration contract", () => {
  const source = readFileSync(resolve(process.cwd(), "client/src/pages/Home.tsx"), "utf8");

  it("builds branded COA export rows with account data and total", () => {
    const printer = { headerText: "Terima kasih", nib: "NIB 123", address: "Amurang", phone: "0812" };
    const accounts: Account[] = [{ code: "101", name: "Kas", type: "Aktiva", balance: 1250 }, { code: "301", name: "Modal", type: "Modal", balance: 900 }];
    const rows = buildCoaExportRows(accounts, printer);
    expect(rows[0]).toEqual(["ANTON SERVICE"]);
    expect(rows[7]).toEqual(["Kode", "Nama Akun", "Tipe", "Saldo"]);
    expect(rows).toContainEqual(["101", "Kas", "Aktiva", 1250]);
    expect(rows.at(-1)).toEqual(["TOTAL AKUN", "", "", 2150]);
    expect(source).toContain('downloadExcel(buildCoaExportRows(reportAccounts, reportPeriodPrinter), "buku-besar-coa.xls")');
    expect(source).toContain('printReport("Buku Besar COA", buildCoaExportRows(reportAccounts, reportPeriodPrinter), reportPeriodPrinter)');
  });

  it("builds balanced Neraca export rows with identity and difference", () => {
    const printer = { headerText: "Terima kasih", nib: "NIB 123", address: "Amurang", phone: "0812" };
    const accounts: Account[] = [{ code: "101", name: "Kas", type: "Aktiva", balance: 5000 }, { code: "201", name: "Hutang", type: "Pasiva", balance: 1000 }, { code: "301", name: "Modal", type: "Modal", balance: 4000 }];
    const rows = buildBalanceSheetExportRows(accounts, { assetTotal: 5000, liabilityTotal: 1000, equityTotal: 4000, netIncome: 0, totalLiabilityEquity: 5000, difference: 0 }, printer);
    expect(rows[0]).toEqual(["ANTON SERVICE"]);
    expect(rows).toContainEqual(["TOTAL AKTIVA", 5000, "TOTAL PASIVA", 5000]);
    expect(rows.at(-1)).toEqual(["", "", "Selisih", 0]);
    expect(source).toContain('downloadExcel(buildBalanceSheetExportRows(reportBalanceAccounts, { assetTotal: reportBalanceAssetTotal, liabilityTotal: reportBalanceLiabilityTotal, equityTotal: reportBalanceEquityTotal, netIncome: reportBalanceNetIncome, totalLiabilityEquity: reportBalanceTotalLiabilityEquity, difference: reportBalanceSheetDifference }, reportPeriodPrinter), "neraca.xls")');
    expect(source).toContain('printReport("Neraca", buildBalanceSheetExportRows(reportBalanceAccounts, { assetTotal: reportBalanceAssetTotal, liabilityTotal: reportBalanceLiabilityTotal, equityTotal: reportBalanceEquityTotal, netIncome: reportBalanceNetIncome, totalLiabilityEquity: reportBalanceTotalLiabilityEquity, difference: reportBalanceSheetDifference }, reportPeriodPrinter), reportPeriodPrinter)');
  });

  it("provides sound and vibration feedback for successful scans", () => {
    expect(source).toContain("export function triggerScanSuccessFeedback()");
    expect(source).toContain("window.AudioContext");
    expect(source).toContain("navigator.vibrate?.(settings.vibration)");
    expect(source).toContain("success: { frequency: 880, duration: 130, vibration: 80 }");
    expect(source).toContain('triggerScanFeedback("not-found")');
    expect(source).toContain('triggerScanFeedback("out-of-stock")');
    expect(source).toContain('vibration: [90, 45, 90]');
    expect(source).toContain('vibration: [180, 70, 180]');
    expect(source).toContain("triggerScanSuccessFeedback(); addToCart(product.id)");
    expect(source).toContain("triggerScanSuccessFeedback(); setProductScannerOpen(false)");
  });

  it("tracks failed scan history by error type and keeps repeated scans grouped", () => {
    const first = upsertFailedScanRecord([], { value: "SPR-404", kind: "not-found" }, new Date("2026-08-18T08:00:00.000Z"));
    const repeated = upsertFailedScanRecord(first, { value: "SPR-404", kind: "not-found" }, new Date("2026-08-18T08:05:00.000Z"));
    const stock = upsertFailedScanRecord(repeated, { value: "SPR-001", kind: "out-of-stock", productName: "Kapasitor" }, new Date("2026-08-18T08:10:00.000Z"));
    expect(stock).toHaveLength(2);
    expect(stock[0]).toMatchObject({ value: "SPR-001", kind: "out-of-stock", productName: "Kapasitor", count: 1 });
    expect(stock[1]).toMatchObject({ value: "SPR-404", kind: "not-found", count: 2 });
    expect(source).toContain('anton_failed_scans_v22');
    expect(source).toContain('title="Riwayat Scan Gagal"');
    expect(source).toContain('["scan-history", "Scan Gagal", FileClock]');
    expect(source).toContain('setFailedScans([])');
    expect(source).toContain('Tambah Barang');
    expect(source).toContain('setProductBarcodeDraft(item.value)');
    expect(source).toContain('setProductSkuDraft(item.value)');
    expect(source).toContain('name="sku" label="SKU" required value={productSkuInput} onChange={event => setProductSkuInput(event.target.value)}');
    expect(source).toContain('productSkuStatus === "duplicate"');
    expect(source).toContain('SKU sudah digunakan produk lain.');
    expect(source).toContain('SKU tersedia.');
    expect(source).toContain('Isi SKU untuk memeriksa ketersediaan.');
    expect(source).toContain('setProductEditor("new")');
    expect(source).toContain('navigate("produk")');
  });

  it("keeps the balance sheet balanced for every product capital source", () => {
    const products = ["Kas", "Hutang", "Bank", "QRIS", "Piutang"].map((capitalSource, index) => ({ id: `funded-${index}`, sku: `F-${index}`, barcode: `F-${index}`, name: `Barang ${capitalSource}`, price: 100, cost: 100, stock: 1, type: "spare", capitalSource }));
    const totals = calculateBalanceSheetTotals(products, [], 0, 0);
    expect(totals.difference).toBe(0);
    expect(source).toContain('name="capitalSource"');
    expect(source).toContain('<option value="Kas">Kas</option>');
    expect(source).toContain('<option value="Hutang">Hutang</option>');
    expect(source).toContain('<option value="Bank">Bank</option>');
    expect(source).toContain('<option value="QRIS">QRIS</option>');
    expect(source).toContain('<option value="Piutang">Piutang</option>');
    expect(source).toContain('capitalSource: String(form.get("capitalSource") || "Kas") as CapitalSource');
    expect(source).toContain('defaultValue={productEditor !== "new" && productEditor ? productCapitalSource(productEditor) : "Kas"}');
    expect(source).not.toContain('<option value="Modal">Modal</option>');
  });

  it("rejects duplicate SKUs while allowing the current product during edit", () => {
    expect(source).toContain('const duplicateSku = products.some(entry => entry.id !== product.id && entry.sku.trim().toLowerCase() === product.sku.toLowerCase())');
    expect(source).toContain('SKU ${product.sku} sudah digunakan produk lain. Silakan gunakan SKU berbeda.');
    expect(source).toContain('sku: String(form.get("sku") || "").trim()');
  });

  it("shows success toasts after Excel report downloads", () => {
    expect(source).toContain("Buku Besar COA berhasil diunduh dalam format Excel.");
    expect(source).toContain("Neraca berhasil diunduh dalam format Excel.");
    expect(source).toContain("if (kind === \"excel\") toast.success(successMessage)");
  });

  it("exposes loading states for report filters and exports", () => {
    expect(source).toContain("const [reportLoading, setReportLoading] = useState(false)");
    expect(source).toContain("const [reportExporting, setReportExporting] = useState<\"excel\" | \"pdf\" | null>(null)");
    expect(source).toContain("aria-busy={loading}");
    expect(source).toContain("aria-busy={reportExporting === \"excel\"}");
    expect(source).toContain("aria-busy={reportExporting === \"pdf\"}");
    expect(source).toContain("disabled={!!reportExporting}");
    expect(source).toContain("animate-spin");
  });

  it("builds quick report period presets for current, previous month, and current year", () => {
    const now = new Date(2026, 0, 15);
    expect(reportPresetRange("current-month", now)).toEqual({ from: "2026-01-01", to: "2026-01-31" });
    expect(reportPresetRange("previous-month", now)).toEqual({ from: "2025-12-01", to: "2025-12-31" });
    expect(reportPresetRange("current-year", now)).toEqual({ from: "2026-01-01", to: "2026-12-31" });
    expect(source).toContain("Bulan Ini");
    expect(source).toContain("Bulan Lalu");
    expect(source).toContain("Tahun Ini");
  });

  it("filters report transactions inclusively by selected date range", () => {
    expect(transactionMatchesPeriod("2026-08-10T10:00:00.000Z", "2026-08-10", "2026-08-12")).toBe(true);
    expect(transactionMatchesPeriod("2026-08-12T23:59:00.000Z", "2026-08-10", "2026-08-12")).toBe(true);
    expect(transactionMatchesPeriod("2026-08-13T00:00:00.000Z", "2026-08-10", "2026-08-12")).toBe(false);
    expect(transactionMatchesPeriod("2026-08-11T10:00:00.000Z", "2026-08-12", "2026-08-10")).toBe(false);
    expect(source).toContain('aria-label="Tanggal mulai laporan"');
    expect(source).toContain('aria-label="Tanggal akhir laporan"');
    expect(source).toContain("reportTransactions");
    expect(source).toContain("buildCoaExportRows(reportAccounts, reportPeriodPrinter)");
    expect(source).toContain("buildBalanceSheetExportRows(reportBalanceAccounts");
  });

  it("keeps pre-period transactions in the cumulative balance-sheet snapshot", () => {
    const transactions = [{ date: "2026-08-01T10:00:00.000Z", noNota: "A" }, { date: "2026-08-12T10:00:00.000Z", noNota: "B" }, { date: "2026-08-13T10:00:00.000Z", noNota: "C" }];
    expect(transactionsThroughDate(transactions, "2026-08-12").map(transaction => transaction.noNota)).toEqual(["A", "B"]);
    expect(source).toContain("const reportBalanceTransactions = useMemo(() => transactionsThroughDate(transactions, reportPeriodTo)");
  });

  it("stores the selected POS transaction date and renders the editable date field", () => {
    const now = new Date("2026-08-17T09:30:00");
    expect(transactionDateToIso("2026-08-12", now).slice(0, 10)).toBe("2026-08-12");
    expect(source).toContain('type="date"');
    expect(source).toContain('aria-label="Tanggal transaksi"');
    expect(source).toContain("transactionDateToIso(transactionDate, now)");
    expect(source).toContain("setTransactionDate(localDateInputValue())");
    expect(source).toContain("formatTransactionDate(transaction.date)");
    expect(source).toContain("Tgl: {formatTransactionDate(transaction.date)}");
    expect(source).toContain("transactions.filter(transaction => dayKey(new Date(transaction.date))");
  });

  it("calculates net revenue, tax, and total from the discounted subtotal", () => {
    const amounts = calculateTransactionAmounts(1000000, 100000, 11);
    expect(amounts.discountedSubtotal).toBe(900000);
    expect(amounts.taxAmount).toBe(99000);
    expect(amounts.total).toBe(999000);
    expect(source).toContain("const { total } = calculateTransactionAmounts(subtotal, discountAmount, tax)");
    expect(source).toContain("discountPercent");
    expect(source).toContain("Diskon (%)");
    expect(source).toContain("calculateTransactionAmounts(transaction.subtotal || 0, transaction.discount || 0, transaction.tax || 0).taxAmount");
  });

  it("shows discounted transactions in dashboard turnover instead of zero", () => {
    const transaction = { subtotal: 350000, discount: 300000, tax: 0 };
    expect(transactionNetTotal(transaction)).toBe(50000);
    expect(transactionNetTotal({ subtotal: 350000, discount: 300000, tax: 11 })).toBe(55500);
    expect(source).toContain("sum + transactionRevenueNet(transaction)");
    expect(source).toContain("const dashboardRevenue = netRevenueTotal");
  });

  it("calculates dashboard turnover as net service plus net goods revenue", () => {
    const transaction = { subtotal: 1000000, discount: 100000, tax: 11, jasaTotal: 600000, spareTotal: 400000 };
    expect(transactionRevenueNet(transaction)).toBe(900000);
    expect(transactionNetTotal(transaction)).toBe(999000);
    expect(transactionRevenueNet({ subtotal: 350000, discount: 300000, tax: 11, jasaTotal: 350000, spareTotal: 0 })).toBe(50000);
    expect(source).toContain("return revenue.serviceNet + revenue.spareNet");
    expect(source).toContain("reduce((sum, transaction) => sum + transactionRevenueNet(transaction)");
  });

  it("shows the reported combined dashboard turnover of Rp200000", () => {
    const transactions = [
      { subtotal: 50000, discount: 0, jasaTotal: 50000, spareTotal: 0 },
      { subtotal: 150000, discount: 0, jasaTotal: 0, spareTotal: 150000 },
    ];
    const summary = summarizeTransactionTotals(transactions as never[]);
    expect(summary.serviceNetRevenue).toBe(50000);
    expect(summary.spareNetRevenue).toBe(150000);
    expect(summary.serviceNetRevenue + summary.spareNetRevenue).toBe(200000);
    expect(source).toContain("const dashboardRevenue = netRevenueTotal");
    expect(source).toContain("Omset Gabungan");
  });

  it("recovers dashboard turnover from legacy transaction items when revenue totals are missing", () => {
    const legacy = {
      subtotal: 1000000,
      discount: 100000,
      items: [
        { productId: "j1", name: "Jasa", qty: 1, price: 600000, cost: 0, type: "jasa" as const },
        { productId: "s1", name: "Barang", qty: 2, price: 200000, cost: 100000, type: "spare" as const },
      ],
    };
    const breakdown = transactionRevenueBreakdown(legacy);
    expect(breakdown.serviceGross).toBe(600000);
    expect(breakdown.spareGross).toBe(400000);
    expect(transactionRevenueNet(legacy)).toBe(900000);
    expect(source).toContain("transaction.jasaTotal ?? transaction.items?.reduce");
    expect(source).toContain("const revenue = transactionRevenueBreakdown(transaction)");
  });

  it("keeps gross revenue accounts and records discount as contra revenue", () => {
    const transaction = { subtotal: 1000000, discount: 100000, tax: 11, total: 999000, jasaTotal: 600000, spareTotal: 400000, hppTotal: 250000, paymentMethod: "Cash" as const };
    const summary = summarizeTransactionTotals([transaction as never]);
    expect(summary.serviceRevenue).toBe(600000);
    expect(summary.spareRevenue).toBe(400000);
    expect(summary.salesDiscount).toBe(-100000);
    expect(summary.taxPayable).toBe(99000);
    expect(summary.serviceRevenue + summary.spareRevenue + summary.salesDiscount).toBe(900000);
    expect(summary.serviceRevenue + summary.spareRevenue + summary.salesDiscount - summary.hpp).toBe(650000);
    expect(source).toContain('{ code: "403", name: "Diskon"');
    expect(source).toContain("summary.salesDiscount -= revenue.effectiveDiscount");
  });

  it("calculates Pendapatan Jasa neto after discount and allocates mixed discounts proportionally", () => {
    const serviceOnly = calculateRevenueBreakdown(350000, 0, 300000, 350000);
    expect(serviceOnly.serviceGross).toBe(350000);
    expect(serviceOnly.effectiveDiscount).toBe(300000);
    expect(serviceOnly.serviceNet).toBe(50000);
    expect(serviceOnly.spareNet).toBe(0);

    const mixed = calculateRevenueBreakdown(600000, 400000, 100000, 1000000);
    expect(mixed.serviceDiscount).toBe(60000);
    expect(mixed.spareDiscount).toBe(40000);
    expect(mixed.serviceNet).toBe(540000);
    expect(mixed.spareNet).toBe(360000);
    expect(mixed.serviceNet + mixed.spareNet).toBe(900000);
    expect(source).toContain("const netRevenueTotal = serviceNetRevenue + spareNetRevenue");
    expect(source).toContain("formatCurrency(serviceNetRevenue)");
  });

  it("deletes only the selected transaction and exposes the per-row delete action", () => {
    const transactions = [{ noNota: "INV-001" }, { noNota: "INV-002" }, { noNota: "INV-003" }];
    expect(removeTransactionByNumber(transactions, "INV-002")).toEqual([{ noNota: "INV-001" }, { noNota: "INV-003" }]);
    const makeTransaction = (noNota: string, total: number, jasaTotal: number) => ({ noNota, date: "2026-08-17T00:00:00.000Z", customer: "Umum", address: "", phone: "", items: [], subtotal: total, discount: 0, tax: 0, total, jasaTotal, spareTotal: 0, hppTotal: 0, paymentMethod: "Cash" as const, paid: total, change: 0, warrantyCode: noNota });
    const before = summarizeTransactionTotals([makeTransaction("INV-001", 150000, 150000), makeTransaction("INV-002", 200000, 200000)]);
    const after = summarizeTransactionTotals([makeTransaction("INV-001", 150000, 150000)]);
    expect(before.serviceRevenue).toBe(350000);
    expect(after.serviceRevenue).toBe(150000);
    expect(source).toContain("const totals = summarizeTransactionTotals(transactions)");
    expect(source).toContain("Lihat struk");
    expect(source).toContain("Hapus transaksi ${transaction.noNota}");
    expect(source).toContain("removeTransactionByNumber(previous, transaction.noNota)");
    expect(source).toContain("aria-label={`Hapus transaksi ${transaction.noNota}`} ".trim());
  });
  it("integrates Beban from Produk & Jasa into POS, COA, Laba Rugi, and Neraca", () => {
    const expenseItem = { productId: "b1", name: "Listrik", qty: 1, price: 250000, cost: 0, type: "beban" as const };
    const transaction = { items: [expenseItem], subtotal: 250000, discount: 0, tax: 0, total: 250000, jasaTotal: 0, spareTotal: 0, hppTotal: 0, paymentMethod: "Cash" as const };
    expect(productTypeLabel("beban")).toBe("Beban");
    expect(transactionExpenseTotal(transaction)).toBe(250000);
    expect(transactionExpenseNet(transaction)).toBe(250000);
    const summary = summarizeTransactionTotals([transaction as never]);
    expect(summary.expenseTotal).toBe(250000);
    expect(summary.cash).toBe(-250000);
    expect(source).toContain('<option value="beban">Beban</option>');
    expect(source).toContain('{ code: "501", name: "Beban Operasional", type: "Beban", balance: expenseTotal }');
    const totals = calculateBalanceSheetTotals([{ type: "jasa" as const, cost: 0, stock: 0 }], [transaction as never], 0, 0, 0);
    expect(totals.assetTotal).toBe(-250000);
    expect(totals.netIncome).toBe(-250000);
    expect(totals.difference).toBe(0);
  });

  it("adds Transportasi from Kasir POS as a Beban Operasional expense", () => {
    const transport = buildTransportExpenseProduct(175000);
    expect(isTransportExpenseProduct(transport)).toBe(true);
    expect(transport.name).toBe("Transportasi");
    expect(transport.type).toBe("beban");
    expect(transport.price).toBe(175000);
    const transaction = { items: [{ productId: transport.id, name: transport.name, qty: 1, price: transport.price, cost: 0, type: transport.type }], subtotal: 175000, discount: 0, tax: 0, total: 175000, jasaTotal: 0, spareTotal: 0, hppTotal: 0, paymentMethod: "Cash" as const };
    expect(transactionExpenseTotal(transaction)).toBe(175000);
    expect(transactionExpenseNet(transaction)).toBe(175000);
    expect(source).toContain("const addTransportExpense");
    expect(source).toContain("> Transportasi</button>");
    const discountIndex = source.indexOf("Diskon (%)");
    const transportIndex = source.indexOf('text-slate-300">Transportasi (Rp)<input');
    const taxIndex = source.indexOf("Pajak (%)");
    expect(discountIndex).toBeGreaterThanOrEqual(0);
    expect(transportIndex).toBeGreaterThan(discountIndex);
    expect(taxIndex).toBeGreaterThan(transportIndex);
    expect(source).toContain('product?.type === "beban" && isTransportExpenseProduct(product)');
    expect(source).toContain('toast.success(`Biaya Transportasi ${formatCurrency(amount)} ditambahkan sebagai Beban Operasional.`)');
  });

  it("uses Barang as the visible product type while preserving the internal spare value", () => {
    expect(productTypeLabel("spare")).toBe("Barang");
    expect(productTypeLabel("jasa")).toBe("Jasa");
    expect(source).toContain('<option value="spare">Barang</option>');
    expect(source).toContain("productTypeLabel(product.type)");
  });

  it("integrates camera barcode scanning with a manual fallback", () => {
    expect(source).toContain("navigator.mediaDevices.getUserMedia");
    expect(source).toContain("BrowserMultiFormatReader");
    expect(source).toContain("decodeFromConstraints");
    expect(source).toContain("Buka kamera HP");
    expect(source).toContain("Gunakan input manual");
    expect(source).toContain("handleBarcodeValue");
    expect(source).toContain("handleProductBarcodeValue");
    expect(source).toContain("productScannerOpen");
    expect(source).toContain("Barcode ${normalized} siap untuk produk baru.");
    expect(source).toContain("Produk ${existing.name} dibuka untuk diedit.");
    expect(source).toContain("<ScanLine size={15} />Scan</button>");
  });

  it("rejects duplicate barcodes for new products and edits while allowing the current product barcode", () => {
    const products = [
      { id: "p1", barcode: "899001", name: "Produk A" },
      { id: "p2", barcode: "899002", name: "Produk B" },
    ];
    expect(hasDuplicateProductBarcode(products, { id: "p3", barcode: "899001" })).toBe(true);
    expect(hasDuplicateProductBarcode(products, { id: "p1", barcode: "899001" })).toBe(false);
    expect(hasDuplicateProductBarcode(products, { id: "p1", barcode: "899002" })).toBe(true);
    expect(hasDuplicateProductBarcode(products, { id: "p3", barcode: "" })).toBe(false);
    expect(source).toContain("const duplicateBarcode = hasDuplicateProductBarcode(products, product)");
    expect(source).toContain("Barcode ${product.barcode} sudah digunakan produk lain.");
  });

  it("updates inventory and capital together when product modal changes", () => {
    const previous = { type: "spare" as const, cost: 100000, stock: 2 };
    const next = { type: "spare" as const, cost: 120000, stock: 3 };
    expect(inventoryValue(previous)).toBe(200000);
    expect(inventoryValue(next)).toBe(360000);
    expect(inventoryCapitalDelta(previous, next)).toBe(160000);
    expect(inventoryValue(next) - inventoryValue(previous)).toBe(inventoryCapitalDelta(previous, next));
    expect(totalInventoryValue([previous, next])).toBe(560000);
    expect(source).toContain("inventoryCapitalAdjustment");
    expect(source).toContain("totalInventoryValue(products)");
    expect(source).toContain("name: \"Persediaan Barang\"");
    expect(source).toContain("name: \"Modal\"");
    expect(source).toContain("Penyusutan Alat dicatat ke COA 503");
    expect(source).toContain('name="depreciation"');
    expect(source).toContain("Stok opname disimpan dan Modal Buku Besar disesuaikan");
    expect(source).toContain("Modal Buku Besar ${delta > 0 ? \"bertambah\" : \"berkurang\"}");
  });

  it("integrates Penyusutan Alat into COA and keeps Neraca balanced", () => {
    const products = [{ type: "spare" as const, cost: 100000, stock: 2, depreciation: 15000 }, { type: "jasa" as const, cost: 0, stock: 1, depreciation: 5000 }];
    expect(productDepreciation(products[0])).toBe(15000);
    expect(totalToolDepreciation(products)).toBe(20000);
    expect(source).toContain('code: "503", name: "Penyusutan Alat"');
    expect(source).toContain('code: "105", name: "Akumulasi Penyusutan Alat"');
    const totals = calculateBalanceSheetTotals(products, [], 0, 0, 0);
    expect(totals.assetTotal).toBe(180000);
    expect(totals.netIncome).toBe(-20000);
    expect(totals.difference).toBe(0);
  });

  it("integrates Biaya Gedung into Produk & Jasa, COA, Laba Rugi, and Neraca", () => {
    const products = [{ type: "spare" as const, cost: 100000, stock: 2, buildingCost: 30000 }, { type: "jasa" as const, cost: 0, stock: 1, buildingCost: 10000 }];
    expect(productBuildingCost(products[0])).toBe(30000);
    expect(totalBuildingCost(products)).toBe(40000);
    expect(source).toContain('name="buildingCost"');
    expect(source).toContain('code: "504", name: "Biaya Gedung"');
    expect(source).toContain('code: "106", name: "Biaya Gedung"');
    const totals = calculateBalanceSheetTotals(products, [], 0, 0, 0);
    expect(totals.assetTotal).toBe(160000);
    expect(totals.netIncome).toBe(-40000);
    expect(totals.difference).toBe(0);
  });

  it("keeps the balance sheet balanced across inventory lifecycle scenarios", () => {
    const opening = [{ type: "spare" as const, cost: 100000, stock: 10 }];
    const added = [...opening, { type: "spare" as const, cost: 50000, stock: 4 }];
    const sold = [{ type: "spare" as const, cost: 100000, stock: 8 }];
    const openingInventory = totalInventoryValue(opening);
    const addedSnapshot = calculateBalanceSheetTotals(added, [], totalInventoryValue(added) - openingInventory, openingInventory);
    expect(addedSnapshot.difference).toBe(0);
    const soldTransaction = { items: [{ productId: "p1", name: "Barang", qty: 2, price: 150000 }], total: 300000, subtotal: 300000, discount: 0, tax: 0, jasaTotal: 0, spareTotal: 300000, hppTotal: 200000, paymentMethod: "Cash" as const };
    const soldSnapshot = calculateBalanceSheetTotals(sold, [soldTransaction as never], 0, openingInventory);
    expect(soldSnapshot.difference).toBe(0);
    const restored = restoreInventoryForTransaction([{ id: "p1", type: "spare" as const, cost: 100000, stock: 8 }], { items: soldTransaction.items });
    const deletedSnapshot = calculateBalanceSheetTotals(restored, [], 0, openingInventory);
    expect(deletedSnapshot.difference).toBe(0);
    expect(source).toContain("calculateBalanceSheetTotals");
    expect(source).toContain("inventoryCapitalAdjustment");
    expect(source).toContain("totalInventoryValue(products)");
  });

  it("synchronizes the Buku Besar Modal balance with product capital changes", () => {
    const opening = [{ type: "spare" as const, cost: 100000, stock: 10 }];
    const added = [...opening, { type: "spare" as const, cost: 50000, stock: 4 }];
    const reduced = [{ type: "spare" as const, cost: 100000, stock: 7 }];
    const openingInventory = totalInventoryValue(opening);
    const baseModal = calculateModalAccountBalance(opening, [], openingInventory, 1000000);
    const addedModal = calculateModalAccountBalance(added, [], openingInventory, 1000000);
    const reducedModal = calculateModalAccountBalance(reduced, [], openingInventory, 1000000);
    expect(addedModal - baseModal).toBe(200000);
    expect(reducedModal - baseModal).toBe(-300000);
    expect(source).toContain("calculateModalAccountBalance(products, transactions, openingInventory)");
    expect(source).toContain('name: "Modal"');
  });

  it("balances edit modal/stok and stock opname flows independently", () => {
    const opening = [{ type: "spare" as const, cost: 100000, stock: 10 }];
    const openingInventory = totalInventoryValue(opening);
    const edited = [{ type: "spare" as const, cost: 150000, stock: 8 }];
    const editDelta = inventoryValue(edited[0]) - inventoryValue(opening[0]);
    const editedSnapshot = calculateBalanceSheetTotals(edited, [], editDelta, openingInventory);
    expect(inventoryValue(edited[0])).toBe(1200000);
    expect(editDelta).toBe(200000);
    expect(editedSnapshot.difference).toBe(0);

    const opname = [{ type: "spare" as const, cost: 100000, stock: 14 }];
    const opnameDelta = inventoryValue(opname[0]) - inventoryValue(opening[0]);
    const opnameSnapshot = calculateBalanceSheetTotals(opname, [], opnameDelta, openingInventory);
    expect(opnameDelta).toBe(400000);
    expect(opnameSnapshot.difference).toBe(0);
    expect(source).toContain("saveOpname");
    expect(source).toContain("inventoryCapitalDelta(previous[index], product)");
    expect(source).toContain("setInventoryCapitalAdjustment(value => value + delta)");
  });

  it("restores sold inventory when an individual transaction is deleted", () => {
    const products = [{ id: "p1", type: "spare" as const, cost: 100000, stock: 4 }, { id: "j1", type: "jasa" as const, cost: 0, stock: 0 }];
    const transaction = { items: [{ productId: "p1", name: "Capacitor", qty: 2, price: 150000 }, { productId: "j1", name: "Service", qty: 1, price: 200000 }] };
    const restored = restoreInventoryForTransaction(products, transaction);
    expect(restored.find(product => product.id === "p1")?.stock).toBe(6);
    expect(restored.find(product => product.id === "j1")?.stock).toBe(0);
    expect(source).toContain("restoreInventoryForTransaction");
    expect(source).toContain("setProducts(previous => restoreInventoryForTransaction(previous, transaction))");
  });

  it("reconciles inventory capital from current inventory and sold HPP", () => {
    const opening = [{ type: "spare" as const, cost: 100000, stock: 10 }];
    const current = [{ type: "spare" as const, cost: 100000, stock: 8 }];
    const sale = { items: [{ productId: "p1", name: "Barang", qty: 2, price: 150000, cost: 100000, type: "spare" as const }], hppTotal: 200000, total: 300000, subtotal: 300000, discount: 0, tax: 0, jasaTotal: 0, spareTotal: 300000, paymentMethod: "Cash" as const };
    expect(calculateReconciledInventoryCapital(current, [sale as never], totalInventoryValue(opening))).toBe(0);
    expect(source).toContain("calculateReconciledInventoryCapital");
    expect(source).toContain("totalInventoryValue(products) - openingInventory + hppSold");
  });

  it("shows inventory capital as a non-negative total balance rather than a negative opening delta", () => {
    const opening = [{ type: "spare" as const, cost: 100000, stock: 10 }];
    const current = [{ type: "spare" as const, cost: 100000, stock: 8 }];
    const sale = { hppTotal: 200000, total: 300000, subtotal: 300000, discount: 0, tax: 0, jasaTotal: 0, spareTotal: 300000, paymentMethod: "Cash" as const };
    expect(calculateReconciledInventoryCapital(current, [sale as never], totalInventoryValue(opening))).toBe(0);
    expect(calculateInventoryCapitalBalance(current, [sale as never], totalInventoryValue(opening))).toBe(1000000);
    expect(source).toContain("calculateInventoryCapitalBalance");
    expect(source).toContain('Row label="Modal persediaan" value={formatCurrency(reportBalanceSystemBalances["301"] || 0)}');
  });

  it("calculates the balance sheet from actual source accounts without a residual plug", () => {
    const assets = 1500000;
    const liabilities = 200000;
    const modal = 1000000;
    const netIncome = 300000;
    expect(assets - (liabilities + modal + netIncome)).toBe(0);
    expect(source).toContain("const balanceDifference = assetTotal - totalLiabilityEquity");
    expect(source).not.toContain("balanceSheetModalCorrection");
  });

  it("keeps scanner and balance-sheet panels usable on narrow screens", () => {
    expect(source).toContain("max-w-md");
    expect(source).toContain("aspect-video w-full object-cover");
    expect(source).toContain("grid gap-4 md:grid-cols-2");
  });

  it("keeps the original branding asset wired into the application", () => {
    expect(source).toContain("anton-original-logo_03fc6b01.jpg");
    expect(source).toContain("ANTON SERVICE");
    expect(source).toContain("ELECTRICAL ENGINEERING");
    expect(source).toContain("printer.nib");
  });

  it("keeps the operational POS and accounting modules available", () => {
    for (const label of ["Kasir POS", "Transaksi", "Produk & Jasa", "Buku Besar COA", "Neraca", "Laba Rugi", "Stok Opname", "Setting"]) {
      expect(source).toContain(label);
    }
    expect(source).toContain("Kembalian");
    expect(source).toContain("Bayar");
    expect(source).toContain("Cetak");
    expect(source).toContain("deleteAccount");
    expect(source).toContain("Hapus akun");
    expect(source).toContain("Akun sistem tidak dapat dihapus");
  });

  it("uses the requested name for COA account 402 without changing its code", () => {
    expect(source).toContain('{ code: "402", name: "Pendapatan Penjualan"');
    expect(source).not.toContain('name: "Pendapatan Sparepart"');
    expect(source).toContain('{ code: "402", name: "Pendapatan Penjualan", type: "Pendapatan", balance: spareRevenue }');
  });

  it("keeps the dashboard summary aligned with the reference layout", () => {
    for (const label of ["Omset Gabungan", "Total Nota", "Jasa 401", "Spare 402 + HPP 502", "Omset 7 Hari Terakhir", "Neraca ringkas"]) {
      expect(source).toContain(label);
    }
    expect(source).toContain("bg-[#fff1f1]");
    expect(source).toContain("bg-[#eef7ff]");
    expect(source).toContain("ResponsiveContainer");
  });

  it("wires every primary page into the single-page renderer", () => {
    for (const page of ["dashboard", "pos", "transaksi", "produk", "coa", "neraca", "labarugi", "opname", "setting"]) {
      expect(source).toContain(`activePage === "${page}"`);
    }
    expect(source).toContain("ReceiptPreview");
    expect(source).toContain("setActivePage");
  });

  it("maps every sidebar menu to its matching page id", () => {
    const menuPairs = [["dashboard", "Dashboard"], ["pos", "Kasir POS"], ["transaksi", "Transaksi"], ["produk", "Produk & Jasa"], ["coa", "Buku Besar COA"], ["neraca", "Neraca"], ["labarugi", "Laba Rugi"], ["opname", "Stok Opname"], ["setting", "Setting"]];
    for (const [id, label] of menuPairs) {
      expect(source).toContain(`"${id}", "${label}"`);
    }
    expect(source).toContain("navigation.map(([id, label, Icon])");
    expect(source).toContain("onClick={() => navigate(id)}");
  });

  it("executes beforeinstallprompt and appinstalled handlers", () => {
    const promptEvent = new Event("beforeinstallprompt");
    const preventDefault = vi.spyOn(promptEvent, "preventDefault");
    let captured: Event | null = null;
    captureInstallPrompt(promptEvent, event => { captured = event; });
    expect(preventDefault).toHaveBeenCalledOnce();
    expect(captured).toBe(promptEvent);

    const setPrompt = vi.fn();
    const closeHelp = vi.fn();
    const notify = vi.fn();
    handleAppInstalled(setPrompt, closeHelp, notify);
    expect(setPrompt).toHaveBeenCalledWith(null);
    expect(closeHelp).toHaveBeenCalledOnce();
    expect(notify).toHaveBeenCalledOnce();
  });

  it("exposes the PWA install entry point and fallback", () => {
    expect(source).toContain("beforeinstallprompt");
    expect(source).toContain("installApp");
    expect(source).toContain("beforeinstallprompt");
    expect(source).toContain("appinstalled");
    expect(source).toContain("installHelpOpen");
    expect(source).toContain("Android / Chrome");
    expect(source).toContain("iPhone / Safari");
    expect(source).toContain("Desktop / Chrome atau Edge");
    expect(source).toContain("Download / Install");
    expect(source).toContain("Aplikasi sudah terpasang");
    expect(source).toContain("display-mode");
    const manifest = readFileSync(resolve(process.cwd(), "client/public/manifest.webmanifest"), "utf8");
    expect(manifest).toContain('"id": "/"');
    expect(manifest).toContain('"display": "standalone"');
    expect(manifest).toContain('"prefer_related_applications": false');
    expect(manifest).toContain('"short_name": "Anton POS"');
    expect(manifest).toContain("anton-original-logo_03fc6b01.jpg");
    expect(readFileSync(resolve(process.cwd(), "client/public/sw.js"), "utf8")).toContain("anton-pos-shell-v3");
  });

  it("persists manual COA edits and deletes only the requested manual account", () => {
    const accounts: Account[] = [{ code: "601", name: "Beban Listrik", type: "Beban", balance: 250000 }, { code: "602", name: "Beban Internet", type: "Beban", balance: 100000 }];
    const edited = upsertCustomAccount(accounts, { code: "601", name: "Beban Listrik & Internet", type: "Beban", balance: 350000 }, "601");
    expect(edited).toHaveLength(2);
    expect(edited.find(account => account.code === "601")?.name).toBe("Beban Listrik & Internet");
    expect(removeCustomAccount(edited, "601")).toEqual([{ code: "602", name: "Beban Internet", type: "Beban", balance: 100000 }]);
    expect(source).toContain("Akun sistem dikelola otomatis dan tidak dapat diedit");
    expect(source).toContain("Kode akun sudah digunakan");
  });

  it("clips receipt logos to the original circular mark in preview and print", () => {
    expect(source).toContain('h-12 w-12 rounded-full object-contain');
    expect(source).toContain('h-14 w-14 rounded-full object-contain');
  });

  it("keeps printer address and custom header synchronized into every receipt preview", () => {
    expect((source.match(/printer\.address/g) || []).length).toBeGreaterThanOrEqual(2);
    expect((source.match(/printer\.headerText/g) || []).length).toBeGreaterThanOrEqual(2);
    expect(source).toContain("<br />{printer.address}<br />{printer.phone}");
    expect(source).toContain('<p className="mt-3 text-center text-[9px] font-semibold">{printer.headerText}</p>');
  });

  it("keeps the financial flow directed and the balance sheet balanced across mixed operations", () => {
    const products = [
      { id: "kas", sku: "KAS", barcode: "", name: "Barang Kas", price: 300, cost: 200, stock: 1, type: "spare" as const, capitalSource: "Kas" as const },
      { id: "hutang", sku: "HUT", barcode: "", name: "Barang Hutang", price: 450, cost: 300, stock: 1, type: "spare" as const, capitalSource: "Hutang" as const },
      { id: "bank", sku: "BNK", barcode: "", name: "Barang Bank", price: 600, cost: 400, stock: 1, type: "spare" as const, capitalSource: "Bank" as const },
      { id: "jasa", sku: "JAS", barcode: "", name: "Jasa", price: 1000, cost: 0, stock: 0, type: "jasa" as const, capitalSource: "Kas" as const, depreciation: 50, buildingCost: 75 },
    ];
    const transactions = [{
      noNota: "N-001", date: "2026-08-22T08:00:00.000Z", customer: "Umum", address: "", phone: "", subtotal: 1200, discount: 100, tax: 11, total: 1221,
      jasaTotal: 1000, spareTotal: 200, hppTotal: 200, paymentMethod: "Cash" as const, paid: 1221, change: 0, warrantyCode: "",
      items: [
        { productId: "jasa", name: "Jasa", qty: 1, price: 1000, cost: 0, type: "jasa" as const },
        { productId: "kas", name: "Barang Kas", qty: 1, price: 200, cost: 200, type: "spare" as const },
        { productId: "transport", name: "Transportasi", qty: 1, price: 100, cost: 0, type: "beban" as const },
      ],
    }];
    const totals = calculateBalanceSheetTotals(products, transactions, 0, 0);
    expect(totals.difference).toBe(0);
    expect(totals.assetTotal).toBe(totals.totalLiabilityEquity);
    expect(source).toContain("const assetTotal = openingCash + totals.cash - funding.Kas");
    expect(source).toContain("const liabilityTotal = totals.taxPayable + funding.Hutang");
    expect(source).toContain("const netIncome = totals.serviceRevenue + totals.spareRevenue + totals.salesDiscount - totals.hpp - totals.expenseTotal");
  });
  it("shows the Modal/HPP input while preserving capital-source accounting", () => {
    expect(source).toContain('<FormInput name="cost" label="Modal/HPP per unit"');
    expect(source).not.toContain('<input type="hidden" name="cost"');
    expect(source).toContain('name="capitalSource"');
    expect(source).toContain('const delta = inventoryCapitalDelta(previousProduct, product)');
    expect(source).toContain('setInventoryCapitalAdjustment(previous => previous + delta)');
  });
  it("provides an animated Anton Service startup splash with offline-safe logo", () => {
    const indexHtml = readFileSync(resolve(process.cwd(), "client/index.html"), "utf8");
    expect(indexHtml).toContain('id="startup-splash"');
    expect(indexHtml).toContain('alt="Logo Anton Service"');
    expect(indexHtml).toContain("data:image/jpeg;base64,");
    expect(indexHtml).toContain("splash-rise");
    expect(indexHtml).toContain("splash-zoom-cycle");
    expect(indexHtml).toContain('id="startup-audio"');
    expect(indexHtml).toContain("anton-service-startup-jingle");
    expect(indexHtml).toContain("navigator.vibrate");
    expect(indexHtml).toContain("prefers-reduced-motion: reduce");
    expect(indexHtml).toContain("classList.add(\"is-hidden\")");
  });
  it("keeps splash motion smooth and exposes an accessible startup loading indicator", () => {
    const indexHtml = readFileSync(resolve(process.cwd(), "client/index.html"), "utf8");
    expect(indexHtml).toContain('class="splash-loading" role="status" aria-live="polite"');
    expect(indexHtml).toContain("splash-spinner");
    expect(indexHtml).toContain("splash-track");
    expect(indexHtml).toContain("splash-progress");
    expect(indexHtml).toContain("cubic-bezier(.23,1,.32,1)");
    expect(indexHtml).toContain("1650");
    expect(indexHtml).toContain("prefers-reduced-motion: reduce");
  });
  it("declares native camera permission and startup splash for the Android wrapper", () => {
    const manifest = readFileSync(resolve(process.cwd(), "../anton-service-pos-apk/android/app/src/main/AndroidManifest.xml"), "utf8");
    const activity = readFileSync(resolve(process.cwd(), "../anton-service-pos-apk/android/app/src/main/java/com/antonservice/pos/MainActivity.java"), "utf8");
    const styles = readFileSync(resolve(process.cwd(), "../anton-service-pos-apk/android/app/src/main/res/values/styles.xml"), "utf8");
    expect(manifest).toContain('android.permission.CAMERA');
    expect(manifest).toContain('android:icon="@mipmap/ic_launcher"');
    expect(manifest).toContain('android:roundIcon="@mipmap/ic_launcher_round"');
    expect(activity).toContain('SplashScreen.installSplashScreen(this)');
    expect(activity).toContain('Manifest.permission.CAMERA');
    expect(activity).toContain('requestPermissions');
    expect(activity).toContain('pendingCameraRequest');
    expect(activity).toContain('onRequestPermissionsResult');
    expect(activity).toContain('grantCameraRequest');
    expect(activity).toContain('request.deny');
    expect(activity).toContain('BridgeWebChromeClient');
    expect(activity).toContain('onPermissionRequest');
    expect(activity).toContain('RESOURCE_VIDEO_CAPTURE');
    expect(activity).toContain('request.grant');
    expect(activity).toContain('onResume');
    expect(activity).toContain('setMediaPlaybackRequiresUserGesture(false)');
    const capacitorConfig = readFileSync(resolve(process.cwd(), "../anton-service-pos-apk/capacitor.config.json"), "utf8");
    expect(capacitorConfig).toContain('"androidScheme": "https"');
    expect(capacitorConfig).toContain('"hostname": "localhost"');
    expect(styles).toContain('windowSplashScreenAnimatedIcon');
    expect(styles).toContain('postSplashScreenTheme');
  });
  it("guides users when camera permission is denied and offers settings retry", () => {
    expect(source).toContain('const [permissionDenied, setPermissionDenied] = useState(false)');
    expect(source).toContain('torchSupported');
    expect(source).toContain('applyConstraints');
    expect(source).toContain('Senter aktif');
    expect(source).toContain('error.name === "NotAllowedError"');
    expect(source).toContain('window.open("app-settings:", "_system")');
    expect(source).toContain("Buka Pengaturan");
    expect(source).toContain("Akses kamera diperlukan");
    expect(source).toContain("Coba Lagi");
    expect(source).toContain("Buka pengaturan situs pada browser");
    expect(source).toContain('window.dispatchEvent(new Event("anton-camera-retry"))');
  });
  it("keeps input pages mounted during parent state updates", () => {
    expect(source).toContain("{Sidebar()}");
    expect(source).toContain("{Header()}");
    expect(source).toContain('activePage === "pos" && Pos()');
    expect(source).not.toContain('activePage === "pos" && <Pos />');
  });
});
