import WidgetKit
import SwiftUI

// MARK: - Model
struct WidgetCategoryModel: Codable {
    let id: String
    let name: String
    let color: String
    let icon: String
    let resourceKey: String?
    var iconData: Data? = nil

    enum CodingKeys: String, CodingKey {
        case id, name, color, icon, resourceKey
    }
}

// MARK: - Colors
private extension Color {
    static let widgetTeal     = Color(red: 0.02,  green: 0.32,  blue: 0.31)   // #055250
    static let widgetSurface  = Color(red: 0.965, green: 0.953, blue: 0.933)  // #F6F3EE
    static let widgetSecondary = Color(red: 0.467, green: 0.353, blue: 0.188) // #775930
}

// MARK: - Model Helpers
private extension WidgetCategoryModel {
    func resolveDisplayName() -> String {
        guard let key = resourceKey else { return name }
        let localized = NSLocalizedString(key, comment: "")
        return localized == key ? name : localized
    }
}

// MARK: - Timeline Provider
struct QuickTransactionProvider: TimelineProvider {
    private let appGroupId = "group.com.lightfeather.masarify"
    private let categoriesKey = "expense_categories"

    func placeholder(in context: Context) -> QuickTransactionEntry {
        QuickTransactionEntry(date: Date(), categories: [
            WidgetCategoryModel(id: "1", name: "Food",      color: "#055250", icon: "", resourceKey: nil),
            WidgetCategoryModel(id: "2", name: "Transport", color: "#055250", icon: "", resourceKey: nil),
            WidgetCategoryModel(id: "3", name: "Shopping",  color: "#055250", icon: "", resourceKey: nil),
        ])
    }

    func getSnapshot(in context: Context, completion: @escaping (QuickTransactionEntry) -> Void) {
        completion(loadEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<QuickTransactionEntry>) -> Void) {
        var entry = loadEntry()
        let group = DispatchGroup()
        for i in entry.categories.indices {
            guard !entry.categories[i].icon.isEmpty,
                  let url = URL(string: entry.categories[i].icon) else { continue }
            group.enter()
            URLSession.shared.dataTask(with: url) { data, _, _ in
                entry.categories[i].iconData = data
                group.leave()
            }.resume()
        }
        group.notify(queue: .main) {
            completion(Timeline(entries: [entry], policy: .never))
        }
    }

    private func loadEntry() -> QuickTransactionEntry {
        let defaults = UserDefaults(suiteName: appGroupId)
        var categories: [WidgetCategoryModel] = []
        if let json = defaults?.string(forKey: categoriesKey),
           let data = json.data(using: .utf8) {
            categories = (try? JSONDecoder().decode([WidgetCategoryModel].self, from: data)) ?? []
        }

        return QuickTransactionEntry(date: Date(), categories: categories)
    }
}

// MARK: - Entry
struct QuickTransactionEntry: TimelineEntry {
    let date: Date
    var categories: [WidgetCategoryModel]
}

// MARK: - Category Button
struct CategoryButtonView: View {
    let category: WidgetCategoryModel
    let deepLinkURL: URL?

    var body: some View {
        let button = VStack(spacing: 5) {
            ZStack {
                Circle()
                    .fill(Color.widgetSurface)
                    .frame(width: 44, height: 44)
                if let data = category.iconData, let uiImage = UIImage(data: data) {
                    Image(uiImage: uiImage)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 22, height: 22)
                        .foregroundColor(Color.widgetSecondary)
                }
            }
            Text(category.resolveDisplayName().uppercased())
                .font(.system(size: 8, weight: .bold))
                .foregroundColor(.primary)
                .lineLimit(1)
                .frame(width: 52)
        }
        if let url = deepLinkURL { Link(destination: url) { button } } else { button }
    }
}

// MARK: - Widget View
struct QuickTransactionView: View {
    let entry: QuickTransactionEntry

    private func deepLinkURL(for category: WidgetCategoryModel) -> URL? {
        var c = URLComponents()
        c.scheme = "masarify"; c.host = "transactions"
        c.queryItems = [
            URLQueryItem(name: "openAddDialog", value: "true"),
            URLQueryItem(name: "type",          value: "expense"),
            URLQueryItem(name: "categoryId",    value: category.id),
        ]
        return c.url
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Masarify")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(Color.widgetSurface)
                    Text("QUICK TRANSACTION")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(Color.widgetSurface)
                        .tracking(1.5)
                }
                Spacer()
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
            

            // Categories horizontal scroll
            if entry.categories.isEmpty {
                Text("Open app to load categories")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .padding(.horizontal, 14)
            } else {
                    HStack(alignment: .top, spacing: 8) {
                        ForEach(entry.categories.prefix(5), id: \.id) { cat in
                            CategoryButtonView(
                                category: cat,
                                deepLinkURL: deepLinkURL(for: cat)
                            )
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.bottom, 12)
                
            }

            Spacer(minLength: 0)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

// MARK: - Color Extension
extension Color {
    init?(hex: String) {
        var s = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        if s.hasPrefix("#") { s.removeFirst() }
        guard s.count == 6, let v = UInt64(s, radix: 16) else { return nil }
        self.init(red: Double((v >> 16) & 0xFF) / 255,
                  green: Double((v >> 8)  & 0xFF) / 255,
                  blue:  Double(v         & 0xFF) / 255)
    }
}

// MARK: - Widget
struct QuickTransactionWidget: Widget {
    let kind = "MasarifyWidget"   // keep for existing placements

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: QuickTransactionProvider()) { entry in
            QuickTransactionView(entry: entry)
                .containerBackground(Color.widgetTeal, for: .widget)
        }
        .configurationDisplayName("Quick Add Expense")
        .description("Tap a category to quickly add an expense.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

// MARK: - Preview
#Preview(as: .systemMedium) {
    QuickTransactionWidget()
} timeline: {
    QuickTransactionEntry(date: Date(), categories: [
        WidgetCategoryModel(id: "1", name: "Food",      color: "#055250", icon: "", resourceKey: nil),
        WidgetCategoryModel(id: "2", name: "Transport", color: "#055250", icon: "", resourceKey: nil),
        WidgetCategoryModel(id: "3", name: "Shopping",  color: "#055250", icon: "", resourceKey: nil),
        WidgetCategoryModel(id: "4", name: "Health",    color: "#055250", icon: "", resourceKey: nil),
        WidgetCategoryModel(id: "5", name: "Dining",    color: "#055250", icon: "", resourceKey: nil),
    ])
}
