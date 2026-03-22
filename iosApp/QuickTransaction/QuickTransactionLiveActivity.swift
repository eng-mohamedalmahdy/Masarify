//
//  QuickTransactionLiveActivity.swift
//  QuickTransaction
//
//  Created by Mahdy on 21/03/2026.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct QuickTransactionAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct QuickTransactionLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: QuickTransactionAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension QuickTransactionAttributes {
    fileprivate static var preview: QuickTransactionAttributes {
        QuickTransactionAttributes(name: "World")
    }
}

extension QuickTransactionAttributes.ContentState {
    fileprivate static var smiley: QuickTransactionAttributes.ContentState {
        QuickTransactionAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: QuickTransactionAttributes.ContentState {
         QuickTransactionAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: QuickTransactionAttributes.preview) {
   QuickTransactionLiveActivity()
} contentStates: {
    QuickTransactionAttributes.ContentState.smiley
    QuickTransactionAttributes.ContentState.starEyes
}
