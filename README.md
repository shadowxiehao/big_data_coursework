# Big Data Group28


## Name
大数据课程的评估练习任务

## Description
任务的主要内容是开发一个基于批处理的文本搜索和过滤管道，通过对大量文本文档和用户定义的查询进行处理，对于每个查询，排名相关性最高的文本文档，并过滤出最终排名中过于相似的文档。每个查询应返回前10个文档作为输出。在处理过程中，需要移除停用词和应用词干提取技术。文档应使用DPH排名模型进行评分。最后，对于每个查询的文档排名进行分析，移除不必要的冗余文档（近似重复文档），如果存在两个文档的标题之间的文本距离（使用提供的比较函数）小于0.5，则只保留其中最相关的文档（基于DPH得分）。评估标准包括代码功能、代码质量、效率以及提交的报告。
数据集包含华盛顿邮报的新闻文章，以及一组查询。有两个版本的数据集：样本和完整数据集。样本数据集是为了允许您使用本地spark部署（如教程中所示）快速迭代设计。完整数据集由约670,000个文档组成。每个查询包含原始查询和查询术语，以及每个查询术语的出现次数。每个新闻文章包含唯一的文章标识符、文章标题、文章内容等信息。本次练习只需要使用'id'、'title'和'contents'字段。
学生需要在提供的Java模板项目上实现必要的Spark函数，将一个包含新闻文章的Dataset<NewsArticle>和一个包含查询的Dataset<Query>转换为一个包含每个查询的10个文档排名的List<DocumentRanking>。解决方案应只包括Spark转换和操作，除了驱动程序中选择执行的任何最终处理外，不应执行任何“离线”计算（例如，预构建搜索索引），即所有处理应在Spark应用程序的生命周期中发生。模板项目提供了一些代码实现以帮助学生进行实现。除此之外，学生需要提交一份短报告，描述解决方案的设计逻辑和实现，并对方案的效率进行讨论。提交的方式包括一个包含最终报告的pdf文件和一个包含代码的单个zip文件。
该练习将根据正确的实现、源代码的质量/可读性、源代码的文档、源代码的效率/可扩展性进行评分，并转换成分数，综合分数在0-25分之间，然后将数字分数转换为字母等级，评分标准如下：
• 正确实现得分12分（部分正确实现得部分分）。
• 源代码的质量/可读性得分3分。
• 源代码文档得分4分。
• 源代码效率/可扩展性得分6分。

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
