#include <chrono>

#include "date.h"

#include "DateUtils.h"

namespace lcp
{
    std::string DateUtils::Iso8601Format = "%Y-%m-%dT%H:%M:%S";

    std::chrono::system_clock::time_point DateUtils::convertISO8601DateToTimePoint(
        const std::string & datetime
    ) {
        date::sys_seconds tmObject{};
        std::istringstream input(datetime);
        input >> date::parse(DateUtils::Iso8601Format, tmObject);

        if (input.fail()) {
            throw std::logic_error("Unable to parse datetime");
        }

        return tmObject;
    }
}
