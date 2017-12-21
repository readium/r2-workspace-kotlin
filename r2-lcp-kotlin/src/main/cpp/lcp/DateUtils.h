#ifndef __DATE_UTILS_H__
#define __DATE_UTILS_H__

#include <string>
#include <chrono>

namespace lcp
{
    class DateUtils
    {
    public:
        static std::string Iso8601Format;

        /**
            Decrypt content

            @return Epoch time in seconds
        */
        static std::chrono::system_clock::time_point convertISO8601DateToTimePoint(
            const std::string & datetime
        );
    };
}

#endif //__DATE_UTILS_H__
